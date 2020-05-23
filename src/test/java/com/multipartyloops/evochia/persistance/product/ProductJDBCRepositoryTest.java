package com.multipartyloops.evochia.persistance.product;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProductJDBCRepositoryTest extends ProductJDBCTest {

    @Mock
    ProductOptionRepository<ProductOptionDto> productOptionRepositoryMock;
    ProductJDBCRepository productJDBCRepository;

    @BeforeEach
    void init() {
        productJDBCRepository = new ProductJDBCRepository(uuidPersistenceTransformer, jdbcTemplate, productOptionRepositoryMock);
    }

    @Test
    void insertProductPopulatesTheProductOptions() {
        ProductCategoryDto productCategoryDto = insertACategory();
        ProductDto productToInsert = new ProductDto(UUID.randomUUID().toString(), productCategoryDto.getProductCategoryId(), "aName", "aDescription", new BigDecimal("3.99"), true, List.of(new ProductOptionDto(), new ProductOptionDto()));

        productJDBCRepository.insertProduct(productToInsert);

        then(productOptionRepositoryMock).should(times(2))
                .insertOption(any(ProductOptionDto.class));

    }


    @Test
    void canGetProductById() {
        ProductCategoryDto productCategoryDto = insertACategory();
        ProductDto productToInsert = new ProductDto(UUID.randomUUID().toString(), productCategoryDto.getProductCategoryId(), "aName", "aDescription", new BigDecimal("3.99"), true, Collections.EMPTY_LIST);
        productJDBCRepository.insertProduct(productToInsert);

        Optional<ProductDto> productById = productJDBCRepository.getProductById(productToInsert.getProductId());

        assertThat(productById.get()).isEqualTo(productToInsert);
    }

    @Test
    void canDeleteAProductById() {
        ProductCategoryDto productCategoryDto = insertACategory();
        ProductDto productToInsert = new ProductDto(UUID.randomUUID().toString(), productCategoryDto.getProductCategoryId(), "aName", "aDescription", new BigDecimal("3.99"), true, Collections.EMPTY_LIST);
        productJDBCRepository.insertProduct(productToInsert);

        productJDBCRepository.deleteProduct(productToInsert.getProductId());

        then(productOptionRepositoryMock).should().deleteAllOptionsOfAProduct(productToInsert.getProductId());
        assertThat(productJDBCRepository.getProductById(productToInsert.getProductId()).isEmpty())
                .isTrue();
    }

    @Test
    void canGetAllProducts() {
        ProductCategoryDto productCategoryDto = insertACategory();
        List<ProductDto> productDtos = insertAFewProductsUnderACategory(productCategoryDto.getProductCategoryId(), true);

        List<ProductDto> allProducts = productJDBCRepository.getAllProducts();

        assertThat(allProducts).containsAll(productDtos);
    }

    @Test
    void canGetAllProductsByCategory(){
        ProductCategoryDto productCategoryOne = insertACategory();
        ProductCategoryDto productCategoryTwo = insertACategory();
        List<ProductDto> productsUnderCategoryOne = insertAFewProductsUnderACategory(productCategoryOne.getProductCategoryId(), true);
        insertAFewProductsUnderACategory(productCategoryTwo.getProductCategoryId(), true);

        List<ProductDto> allProducts = productJDBCRepository.getProductsByCategory(productCategoryOne.getProductCategoryId());

        assertThat(allProducts).containsExactlyInAnyOrder(productsUnderCategoryOne.toArray(new ProductDto[0]));
    }

    @Test
    void canGetAllEnabledProductsByCategory(){
        ProductCategoryDto productCategoryOne = insertACategory();
        List<ProductDto> enabledProducts = insertAFewProductsUnderACategory(productCategoryOne.getProductCategoryId(), true);
        List<ProductDto> notEnabledProducts = insertAFewProductsUnderACategory(productCategoryOne.getProductCategoryId(), false);

        List<ProductDto> allProducts = productJDBCRepository.getEnabledProductsByCategory(productCategoryOne.getProductCategoryId());

        assertThat(enabledProducts).containsExactlyInAnyOrder(allProducts.toArray(new ProductDto[0]));
    }

    @Test
    void canUpdateAProduct(){
        ProductCategoryDto productCategoryOne = insertACategory();
        List<ProductDto> products = insertAFewProductsUnderACategory(productCategoryOne.getProductCategoryId(), true);
        ProductDto productToUpdate = products.get(0);
        ProductDto updated = new ProductDto(productToUpdate.getProductId(),productToUpdate.getCategoryId(),"anUpdatedName","anUpdatedDescription", BigDecimal.valueOf(1.11),false, productToUpdate.getProductOptions());

        productJDBCRepository.updateProduct(updated);

        assertThat(productJDBCRepository.getProductById(productToUpdate.getProductId()).get())
                .isEqualTo(updated);
    }

    @Test
    void aCategoryOfAProductCanBeUpdated(){
        ProductCategoryDto from = insertACategory();
        ProductCategoryDto to = insertACategory();
        List<ProductDto> products = insertAFewProductsUnderACategory(from.getProductCategoryId(), true);

        ProductDto productToUpdate = products.get(0);

        productJDBCRepository.updateProductsCategory(productToUpdate.getProductId(), to.getProductCategoryId());

        assertThat(productJDBCRepository.getProductById(productToUpdate.getProductId()).get().getCategoryId()).isEqualTo(to.getProductCategoryId());
    }

    @Test
    void aCategoryOfAProductCanNotBeUpdatedWhenNewCategoryDoesNotExist(){
        ProductCategoryDto from = insertACategory();
        List<ProductDto> products = insertAFewProductsUnderACategory(from.getProductCategoryId(), true);

        ProductDto productToUpdate = products.get(0);

        assertThatThrownBy(()->productJDBCRepository.updateProductsCategory(productToUpdate.getProductId(), UUID.randomUUID().toString()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    private List<ProductDto> insertAFewProductsUnderACategory(String categoryId, Boolean isEnabled){

        return IntStream.range(0,5)
                .mapToObj(_i->insertAProduct(categoryId, isEnabled))
                .collect(Collectors.toList());
    }

}