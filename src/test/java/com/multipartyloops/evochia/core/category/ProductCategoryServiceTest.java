package com.multipartyloops.evochia.core.category;

import com.multipartyloops.evochia.core.product.category.ProductCategoryService;
import com.multipartyloops.evochia.core.product.dto.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.exceptions.InvalidCategoryNameException;
import com.multipartyloops.evochia.core.product.exceptions.InvalidProductCategoryId;
import com.multipartyloops.evochia.core.product.exceptions.ProductCategoryCouldNotBeFoundException;
import com.multipartyloops.evochia.persistance.product.category.ProductCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    public static final String A_PRODUCT_CATEGORY_ID = UUID.randomUUID().toString();
    @Mock
    private ProductCategoryRepository<ProductCategoryDto> productCategoryRepositoryMock;

    private ProductCategoryService productCategoryService;

    @BeforeEach
    void setUp() {
        productCategoryService = new ProductCategoryService(productCategoryRepositoryMock);
    }

    @Test
    void getByIdThrowsInvalidProductCategoryIdWhenIdIsNull(){

        assertThatThrownBy(()->productCategoryService.getById(null))
                .hasMessage("Error related to the product category id provided")
                .isInstanceOf(InvalidProductCategoryId.class);
    }

    @Test
    void getByIdThrowsInvalidProductCategoryIdWhenIdIsEmpty(){

        assertThatThrownBy(()->productCategoryService.getById(""))
                .hasMessage("Error related to the product category id provided")
                .isInstanceOf(InvalidProductCategoryId.class);
    }

    @Test
    void getByIdThrowsExceptionWhenRepositoryReturnsAnEmptyOptional(){
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID))
                .willReturn(Optional.empty());

        assertThatThrownBy(()->productCategoryService.getById(A_PRODUCT_CATEGORY_ID))
                .isInstanceOf(ProductCategoryCouldNotBeFoundException.class)
                .hasMessage("Product category not found");
    }

    @Test
    void getByIdReturnsValueFromRepository(){
        ProductCategoryDto retrievedByRepository = new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", true);
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID))
                .willReturn(Optional.of(retrievedByRepository));

        ProductCategoryDto byId = productCategoryService.getById(A_PRODUCT_CATEGORY_ID);

        assertThat(byId).isEqualTo(retrievedByRepository);
    }

    @Test
    void returnsAListOfAllCategoriesFoundInTheDatabaseEnabledFirst() {

        given(productCategoryRepositoryMock.getAllProductCategories()).willReturn(listOfCategories());

        List<ProductCategoryDto> list = productCategoryService.getAll();

        assertThat(list).containsExactly(sortedListOfCategories().toArray(new ProductCategoryDto[0]));
    }

    @Test
    void canUpdateACategory() {

        productCategoryService.update(A_PRODUCT_CATEGORY_ID, "an_updated_name", true);

        then(productCategoryRepositoryMock).should().updateProductCategory(new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "an_updated_name", true));
    }

    @Test
    void canDeleteACategory() {

        productCategoryService.delete(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().deleteProductCategoryById(A_PRODUCT_CATEGORY_ID);
    }

    @Test
    void enableById_enableACategory() {
        ProductCategoryDto retrievedFromDb = new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", false);
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.of(retrievedFromDb));

        productCategoryService.enableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().updateProductCategory(new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", true));
    }

    @Test
    void enableById_doNothingWhenCategoryIsEnabled() {
        ProductCategoryDto retrievedFromDb = new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", true);
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.of(retrievedFromDb));

        productCategoryService.enableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().getProductCategoryById(A_PRODUCT_CATEGORY_ID);
        then(productCategoryRepositoryMock).shouldHaveNoMoreInteractions();
    }

    @Test
    void enableById_doNothingWhenCategoryNotFound() {

        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.empty());

        productCategoryService.enableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().getProductCategoryById(A_PRODUCT_CATEGORY_ID);
        then(productCategoryRepositoryMock).shouldHaveNoMoreInteractions();
    }

    @Test
    void disableById_enableACategory() {
        ProductCategoryDto retrievedFromDb = new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", true);
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.of(retrievedFromDb));

        productCategoryService.disableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().updateProductCategory(new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", false));
    }

    @Test
    void disableById_doNothingWhenCategoryIsDisabled() {
        ProductCategoryDto retrievedFromDb = new ProductCategoryDto(A_PRODUCT_CATEGORY_ID, "aName", false);
        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.of(retrievedFromDb));

        productCategoryService.disableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().getProductCategoryById(A_PRODUCT_CATEGORY_ID);
        then(productCategoryRepositoryMock).shouldHaveNoMoreInteractions();
    }

    @Test
    void disableById_doNothingWhenCategoryNotFound() {

        given(productCategoryRepositoryMock.getProductCategoryById(A_PRODUCT_CATEGORY_ID)).willReturn(Optional.empty());

        productCategoryService.disableById(A_PRODUCT_CATEGORY_ID);

        then(productCategoryRepositoryMock).should().getProductCategoryById(A_PRODUCT_CATEGORY_ID);
        then(productCategoryRepositoryMock).shouldHaveNoMoreInteractions();
    }

    @Test
    void addNewCategory() {
        ProductCategoryDto categoryToAdd = new ProductCategoryDto(null, "a name", true);

        productCategoryService.addNewCategory(categoryToAdd);

        ArgumentCaptor<ProductCategoryDto> argumentCaptor = ArgumentCaptor.forClass(ProductCategoryDto.class);
        then(productCategoryRepositoryMock).should().addProductCategory(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getProductCategoryName()).isEqualTo("a name");
        assertThat(argumentCaptor.getValue().getProductCategoryId()).isNotNull();
        assertThat(argumentCaptor.getValue().isEnabled()).isTrue();
    }

    @Test
    void addNewCategoryThrowsExceptionWhenNameIsNull() {
        ProductCategoryDto categoryToAdd = new ProductCategoryDto(null, null, true);

        assertThatThrownBy(() -> productCategoryService.addNewCategory(categoryToAdd))
                .isInstanceOf(InvalidCategoryNameException.class)
                .hasMessage("Category name should be provided");

    }

    @Test
    void addNewCategoryThrowsExceptionWhenNameIsAnEmptyString() {
        ProductCategoryDto categoryToAdd = new ProductCategoryDto(null, "", true);

        assertThatThrownBy(() -> productCategoryService.addNewCategory(categoryToAdd))
                .isInstanceOf(InvalidCategoryNameException.class)
                .hasMessage("Category name should be provided");

    }

    @Test
    void getAllEnabledCategoriesAreSortedByName(){
        given(productCategoryRepositoryMock.getAllEnabledProductCategories()).willReturn(listOfEnabledCategories());

        List<ProductCategoryDto> allEnabledCategories = productCategoryService.getAllEnabledCategories();

        assertThat(allEnabledCategories).containsExactly(listOfSortedEnabledCategories().toArray(new ProductCategoryDto[0]));
    }


    private List<ProductCategoryDto> listOfCategories() {
        return new ArrayList<>(List.of(
                new ProductCategoryDto("d5094e0b-c77e-4615-9f72-583ac12cf416", "f_Name", true),
                new ProductCategoryDto("c2724e82-6d91-4884-af4c-c6b681582011", "a_Name", false),
                new ProductCategoryDto("1cd7f730-9f9b-4932-b453-d36e729ba16a", "c_Name", true),
                new ProductCategoryDto("6fbff9e7-a2ff-42df-9cfd-61a5f071d288", "b_Name", false),
                new ProductCategoryDto("0e9c9d97-3d56-4e9d-8dcf-e02e08fea463", "f_Name", false)
        ));
    }

    private List<ProductCategoryDto> listOfEnabledCategories() {
        return new ArrayList<>(List.of(
                new ProductCategoryDto("d5094e0b-c77e-4615-9f72-583ac12cf416", "f_Name", true),
                new ProductCategoryDto("c2724e82-6d91-4884-af4c-c6b681582011", "a_Name", true),
                new ProductCategoryDto("1cd7f730-9f9b-4932-b453-d36e729ba16a", "c_Name", true),
                new ProductCategoryDto("6fbff9e7-a2ff-42df-9cfd-61a5f071d288", "b_Name", true),
                new ProductCategoryDto("0e9c9d97-3d56-4e9d-8dcf-e02e08fea463", "f_Name", true)
        ));
    }
    private List<ProductCategoryDto> listOfSortedEnabledCategories() {
        return new ArrayList<>(List.of(
                new ProductCategoryDto("c2724e82-6d91-4884-af4c-c6b681582011", "a_Name", true),
                new ProductCategoryDto("6fbff9e7-a2ff-42df-9cfd-61a5f071d288", "b_Name", true),
                new ProductCategoryDto("1cd7f730-9f9b-4932-b453-d36e729ba16a", "c_Name", true),
                new ProductCategoryDto("d5094e0b-c77e-4615-9f72-583ac12cf416", "f_Name", true),
                new ProductCategoryDto("0e9c9d97-3d56-4e9d-8dcf-e02e08fea463", "f_Name", true)
        ));
    }

    private List<ProductCategoryDto> sortedListOfCategories() {
        return new ArrayList<>(List.of(
                new ProductCategoryDto("1cd7f730-9f9b-4932-b453-d36e729ba16a", "c_Name", true),
                new ProductCategoryDto("d5094e0b-c77e-4615-9f72-583ac12cf416", "f_Name", true),
                new ProductCategoryDto("c2724e82-6d91-4884-af4c-c6b681582011", "a_Name", false),
                new ProductCategoryDto("6fbff9e7-a2ff-42df-9cfd-61a5f071d288", "b_Name", false),
                new ProductCategoryDto("0e9c9d97-3d56-4e9d-8dcf-e02e08fea463", "f_Name", false)
        ));
    }


}