package com.multipartyloops.evochia.persistance.product.option;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.persistance.product.ProductJDBCTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class ProductOptionJDBCRepositoryTest extends ProductJDBCTest {


    private ProductOptionJDBCRepository productOptionJDBCRepository;


    @BeforeEach
    void init() {
        productOptionJDBCRepository = new ProductOptionJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);
    }

    @Test
    void storesAndRetrievedAnOption() {
        ProductCategoryDto productCategoryDto = insertACategory();
        ProductDto productDto = insertAProduct(productCategoryDto.getProductCategoryId());
        ProductOptionDto productOptionDto = new ProductOptionDto(
                UUID.randomUUID().toString(),
                productDto.getProductId(),
                "aVariation",
                BigDecimal.valueOf(0.55)
        );

        productOptionJDBCRepository.insertOption(productOptionDto);

        assertThat(productOptionJDBCRepository.getProductOptionById(productOptionDto.getProductOptionId()).get())
                .isEqualTo(productOptionDto);
    }

    @Test
    void canGetOptionsOfAProduct() {

        List<ProductOptionDto> inserted = storesOptionsOfAProduct();

        List<ProductOptionDto> retrieved = productOptionJDBCRepository.getOptionsByProductId(inserted.get(0).getProductId());

        assertThat(retrieved).containsAll(inserted);
    }

    @Test
    void canDeleteEntriesByOptionsId() {
        List<ProductOptionDto> productOptionDtos = storesOptionsOfAProduct();
        ProductOptionDto toDelete = productOptionDtos.get(2);

        productOptionJDBCRepository.deleteOptionById(toDelete.getProductOptionId());

        assertThat(productOptionJDBCRepository.getProductOptionById(toDelete.getProductOptionId()).isEmpty())
                .isTrue();
    }

    @Test
    void canDeleteAllEntriesByProductId() {
        List<ProductOptionDto> productOptionDtos = storesOptionsOfAProduct();
        String toDelete = productOptionDtos.get(2).getProductId();

        productOptionJDBCRepository.deleteAllOptionsOfAProduct(toDelete);

        assertThat(productOptionJDBCRepository.getOptionsByProductId(toDelete).size())
                .isEqualTo(0);
    }


    List<ProductOptionDto> storesOptionsOfAProduct() {
        ProductCategoryDto productCategoryDto = insertACategory();
        ProductDto productDto = insertAProduct(productCategoryDto.getProductCategoryId());
        List<ProductOptionDto> listOfOptions = IntStream.range(0, 4).mapToObj(_i -> new ProductOptionDto(
                UUID.randomUUID().toString(),
                productDto.getProductId(),
                "aVariation",
                BigDecimal.valueOf(0.55)
        )).collect(Collectors.toList());

        listOfOptions.forEach(productOption -> productOptionJDBCRepository.insertOption(productOption));

        return listOfOptions;
    }


}