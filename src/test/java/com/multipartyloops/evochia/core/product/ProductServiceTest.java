package com.multipartyloops.evochia.core.product;

import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.core.product.exceptions.CategoryDoesNotExistException;
import com.multipartyloops.evochia.core.product.exceptions.MandatoryFieldNotPassedException;
import com.multipartyloops.evochia.core.product.exceptions.ProductNotFoundException;
import com.multipartyloops.evochia.persistance.product.ProductRepository;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    public static final String A_PRODUCT_ID = UUID.randomUUID().toString();
    public static final String A_CATEGORY_ID = UUID.randomUUID().toString();
    public static final String A_PRODUCT_NAME = "a_product_name";
    public static final String A_DESCRIPTION = "a description";
    public static final String A_PRODUCT_VARIATION = "aProductVariation";

    @Mock
    private ProductRepository<ProductDto> productRepositoryMock;

    @Mock
    private ProductOptionRepository<ProductOptionDto> productOptionRepositoryMock;

    private ProductService productService;

    @BeforeEach
    void init() {
        productService = new ProductService(productRepositoryMock, productOptionRepositoryMock);
    }

    @Test
    void canInsertAProduct() {

        ProductDto passed = new ProductDto(null, A_CATEGORY_ID, A_PRODUCT_NAME, A_DESCRIPTION, BigDecimal.valueOf(1.11), true, Collections.emptyList());

        productService.addProduct(passed);

        ArgumentCaptor<ProductDto> productDtoArgumentCaptor = ArgumentCaptor.forClass(ProductDto.class);
        then(productRepositoryMock).should().insertProduct(productDtoArgumentCaptor.capture());
        ProductDto productPassedToRepository = productDtoArgumentCaptor.getValue();
        passed.setProductId(productPassedToRepository.getProductId());
        assertThat(passed).isEqualTo(productPassedToRepository);
    }


    @Test
    void insertProductThrowsExceptionWhenTheCategoryIdIsNotPassed() {
        ProductDto missingCategoryId = new ProductDto(null, null, A_PRODUCT_NAME, A_DESCRIPTION, BigDecimal.valueOf(1.11), true, Collections.emptyList());

        assertThatThrownBy(() -> productService.addProduct(missingCategoryId))
                .hasMessage("Product needs to have a category")
                .isInstanceOf(MandatoryFieldNotPassedException.class);
    }

    @Test
    void insertProductThrowsExceptionWhenTheNameIsNotPassed() {
        ProductDto missingName = new ProductDto(null, A_CATEGORY_ID, null, A_DESCRIPTION, BigDecimal.valueOf(1.11), true, Collections.emptyList());

        assertThatThrownBy(() -> productService.addProduct(missingName))
                .hasMessage("Product needs to have a name")
                .isInstanceOf(MandatoryFieldNotPassedException.class);
    }

    @Test
    void insertProductThrowsExceptionWhenThePriceIsNotPassed() {
        ProductDto missingPrice = new ProductDto(null, A_CATEGORY_ID, A_PRODUCT_NAME, A_DESCRIPTION, null, true, Collections.emptyList());

        assertThatThrownBy(() -> productService.addProduct(missingPrice))
                .hasMessage("Product needs to have a price")
                .isInstanceOf(MandatoryFieldNotPassedException.class);
    }

    @Test
    void whenAProductIsDeletedAllAssociatedOptionsShouldBeDeleted() {

        productService.deleteProduct(A_PRODUCT_ID);

        then(productOptionRepositoryMock).should().deleteAllOptionsOfAProduct(A_PRODUCT_ID);
        then(productRepositoryMock).should().deleteProduct(A_PRODUCT_ID);
    }

    @Test
    void returnsAllProductsByCategory() {

        List<ProductDto> products = aListOfProducts();
        when(productRepositoryMock.getProductsByCategory(A_CATEGORY_ID)).thenReturn(products);

        List<ProductDto> productsByCategory = productService.getAllProductsByCategory(A_CATEGORY_ID);

        assertThat(productsByCategory).containsAll(products);
    }

    @Test
    void returnsAllProducts() {
        List<ProductDto> products = aListOfProducts();
        when(productRepositoryMock.getAllProducts()).thenReturn(products);

        List<ProductDto> allProducts = productService.getAllProducts();
        assertThat(allProducts).containsAll(products);
    }

    @Test
    void returnsAllEnabledProductsByCategory() {
        List<ProductDto> products = aListOfProducts();
        when(productRepositoryMock.getEnabledProductsByCategory(A_CATEGORY_ID)).thenReturn(products);

        List<ProductDto> productsByCategory = productService.getEnabledProductsByCategory(A_CATEGORY_ID);

        assertThat(productsByCategory).containsAll(products);
    }

    @Test
    void aProductNotFoundExceptionIsThrownWhenAProductCannotBeFoundById() {

        when(productRepositoryMock.getProductById(A_PRODUCT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(A_PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found");

    }

    @Test
    void aProductCanBeRetrievedById() {
        ProductDto aProduct = new ProductDto(A_PRODUCT_ID, UUID.randomUUID().toString(), "aname", "adesription", BigDecimal.valueOf(10.11), true, Collections.emptyList());
        when(productRepositoryMock.getProductById(A_PRODUCT_ID)).thenReturn(Optional.of(aProduct));

        ProductDto actual = productService.getProductById(A_PRODUCT_ID);
        assertThat(actual).isEqualTo(aProduct);
    }

    @Test
    void productUpdateThrowsExceptionWhenIdIsNotPassed() {
        ProductDto aProductWithoutId = new ProductDto(null, UUID.randomUUID().toString(), "aname", "adesription", BigDecimal.valueOf(10.11), true, Collections.emptyList());

        assertThatThrownBy(() -> productService.updateProduct(null, null, null, null, null, null))
                .isInstanceOf(MandatoryFieldNotPassedException.class)
                .hasMessage("Cannot update product without productId");
    }

    @Test
    void productOptionsAreUpdatedWithTheProductWhenNotNull() {

        List<ProductOptionDto> productOptions = aListOfProductOptions();
        given(productRepositoryMock.getProductById(A_PRODUCT_ID)).willReturn(Optional.of(new ProductDto()));

        productService.updateProduct(A_PRODUCT_ID, null, null, null, null, productOptions);

        then(productOptionRepositoryMock).should().deleteAllOptionsOfAProduct(A_PRODUCT_ID);
        productOptions.forEach(option -> {
            then(productOptionRepositoryMock).should().insertOption(option);
        });
    }

    @Test
    void productCanBeUpdated() {
        ProductDto aProductDto = new ProductDto(A_PRODUCT_ID, A_CATEGORY_ID, A_PRODUCT_NAME, A_DESCRIPTION, BigDecimal.valueOf(10.99), true, aListOfProductOptions());
        when(productRepositoryMock.getProductById(A_PRODUCT_ID)).thenReturn(Optional.of(aProductDto));

        productService.updateProduct(A_PRODUCT_ID, "aNewName", "anUpdatedDescription", BigDecimal.valueOf(7.77), false, null);

        then(productRepositoryMock).should().updateProduct(new ProductDto(A_PRODUCT_ID, A_CATEGORY_ID, "aNewName", "anUpdatedDescription", BigDecimal.valueOf(7.77), false, null));
    }

    @Test
    void updateWillUpdateOnlyNonNullValues() {
        ProductDto aProductDto = new ProductDto(A_PRODUCT_ID, A_CATEGORY_ID, A_PRODUCT_NAME, A_DESCRIPTION, BigDecimal.valueOf(10.99), true, aListOfProductOptions());
        when(productRepositoryMock.getProductById(A_PRODUCT_ID)).thenReturn(Optional.of(aProductDto));

        productService.updateProduct(A_PRODUCT_ID, null, null, null, null, null);

        then(productOptionRepositoryMock).shouldHaveNoMoreInteractions();
        then(productRepositoryMock).should().updateProduct(new ProductDto(A_PRODUCT_ID, A_CATEGORY_ID, A_PRODUCT_NAME, A_DESCRIPTION, BigDecimal.valueOf(10.99), true, null));
    }


    @Test
    void updateCategoryThrowsAnExceptionIfCategoryDoesNotExist() {

        doThrow(new DataIntegrityViolationException("a message")).when(productRepositoryMock).updateProductsCategory(A_PRODUCT_ID, A_CATEGORY_ID);

        assertThatThrownBy(() -> productService.changeCategoryOfAProduct(A_PRODUCT_ID, A_CATEGORY_ID))
                .isInstanceOf(CategoryDoesNotExistException.class)
                .hasMessage("Category Id does not exist");
    }

    @Test
    void categoryOfAProductCanChange() {

        productService.changeCategoryOfAProduct(A_PRODUCT_ID, A_CATEGORY_ID);

        then(productRepositoryMock).should().updateProductsCategory(A_PRODUCT_ID, A_CATEGORY_ID);
    }

    private List<ProductOptionDto> aListOfProductOptions() {
        return List.of(new ProductOptionDto(UUID.randomUUID().toString(), A_PRODUCT_ID, A_PRODUCT_VARIATION, null),
                new ProductOptionDto(UUID.randomUUID().toString(), A_PRODUCT_ID, A_PRODUCT_VARIATION, null));
    }


    private List<ProductDto> aListOfProducts() {
        return List.of(
                new ProductDto(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "aname", "adesription", BigDecimal.valueOf(10.11), true, Collections.emptyList()),
                new ProductDto(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "aname", "adesription", BigDecimal.valueOf(10.11), true, Collections.emptyList())
        );
    }
}