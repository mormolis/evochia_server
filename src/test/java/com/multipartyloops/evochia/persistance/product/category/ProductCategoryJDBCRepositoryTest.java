package com.multipartyloops.evochia.persistance.product.category;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.product.ProductJDBCRepository;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionJDBCRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProductCategoryJDBCRepositoryTest extends JDBCTest {


    @Mock
    private ProductOptionJDBCRepository productOptionJDBCRepositoryMock;
    @Mock
    private ProductJDBCRepository productJDBCRepositoryMock;

    private ProductCategoryJDBCRepository productCategoryJDBCRepository;

    @BeforeEach
    void init() {
        productCategoryJDBCRepository = new ProductCategoryJDBCRepository(new JdbcTemplate(dataSource), new UuidPersistenceTransformer(), productJDBCRepositoryMock, productOptionJDBCRepositoryMock);
    }


    @Test
    void addsAndRetrievesProductCategories() {
        ProductCategoryDto productCategoryDto = new ProductCategoryDto(aCategoryId(), "aCategoryName", true);

        productCategoryJDBCRepository.addProductCategory(productCategoryDto);

        assertThat(productCategoryJDBCRepository.getProductCategoryById(productCategoryDto.getProductCategoryId()).get())
                .isEqualTo(productCategoryDto);
    }

    @Test
    void retrievesAllCategories() {
        List<ProductCategoryDto> categories = getRandomCategories(5);

        categories.forEach(category -> productCategoryJDBCRepository.addProductCategory(category));

        assertThat(productCategoryJDBCRepository.getAllProductCategories()).containsAll(categories);
    }

    @Test
    void retrievesAllEnabledCategories() {
        List<ProductCategoryDto> categories = getRandomCategories(5);
        List<ProductCategoryDto> disabled = categories.stream().filter(c -> !c.isEnabled()).collect(Collectors.toList());
        List<ProductCategoryDto> enabled = categories.stream().filter(ProductCategoryDto::isEnabled).collect(Collectors.toList());

        categories.forEach(category -> productCategoryJDBCRepository.addProductCategory(category));

        assertThat(productCategoryJDBCRepository.getAllEnabledProductCategories())
                .doesNotContain(disabled.toArray(new ProductCategoryDto[0]));
        assertThat(productCategoryJDBCRepository.getAllEnabledProductCategories())
                .containsAll(enabled);
    }

    @Test
    void retrievesAllDisabledCategories() {
        List<ProductCategoryDto> categories = getRandomCategories(5);
        List<ProductCategoryDto> disabled = categories.stream().filter(c -> !c.isEnabled()).collect(Collectors.toList());
        List<ProductCategoryDto> enabled = categories.stream().filter(ProductCategoryDto::isEnabled).collect(Collectors.toList());

        categories.forEach(category -> productCategoryJDBCRepository.addProductCategory(category));

        assertThat(productCategoryJDBCRepository.getAllDisabledProductCategories())
                .doesNotContain(enabled.toArray(new ProductCategoryDto[0]));
        assertThat(productCategoryJDBCRepository.getAllDisabledProductCategories())
                .containsAll(disabled);
    }

    @Test
    void deletesACategory() {
        List<ProductCategoryDto> categories = getRandomCategories(4);
        ProductCategoryDto deleted = categories.get(2);
        categories.forEach(category -> productCategoryJDBCRepository.addProductCategory(category));
        ProductDto aProduct = new ProductDto();
        aProduct.setProductId("aProductId");
        given(productJDBCRepositoryMock.getProductsByCategory(any(String.class))).willReturn(List.of(aProduct));

        productCategoryJDBCRepository.deleteProductCategoryById(deleted.getProductCategoryId());

        then(productJDBCRepositoryMock).should().deleteProduct("aProductId");
        then(productOptionJDBCRepositoryMock).should().deleteAllOptionsOfAProduct("aProductId");
        assertThat(productCategoryJDBCRepository.getProductCategoryById(deleted.getProductCategoryId()).isEmpty()).isTrue();
    }

    private static String aCategoryId() {
        return UUID.randomUUID().toString();
    }

    private List<ProductCategoryDto> getRandomCategories(int numberOfCategories) {
        return IntStream.range(0, numberOfCategories)
                .mapToObj(i -> new ProductCategoryDto(aCategoryId(), aCategoryId(), i % 2 == 0))
                .collect(Collectors.toList());
    }
}