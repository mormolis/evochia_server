package com.multipartyloops.evochia.persistance.product;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.persistance.JDBCTest;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public abstract class ProductJDBCTest extends JDBCTest {

    protected JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    protected UuidPersistenceTransformer uuidPersistenceTransformer = new UuidPersistenceTransformer();

    protected ProductDto insertAProduct(String categoryId) {
        ProductDto productDto = new ProductDto(
                UUID.randomUUID().toString(),
                categoryId,
                UUID.randomUUID().toString(),
                "aDescription",
                BigDecimal.valueOf(4.99),
                true,
                new ArrayList<>()
        );

        String sql = "INSERT INTO product (product_id, category_id, name, description, price, enabled) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                uuidPersistenceTransformer.fromString(productDto.getProductId()),
                uuidPersistenceTransformer.fromString(productDto.getCategoryId()),
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.isEnabled()
        );
        return productDto;
    }

    protected ProductDto insertAProduct(String categoryId, boolean isEnabled) {
        ProductDto productDto = new ProductDto(
                UUID.randomUUID().toString(),
                categoryId,
                UUID.randomUUID().toString(),
                "aDescription",
                BigDecimal.valueOf(4.99),
                isEnabled,
                new ArrayList<>()
        );

        String sql = "INSERT INTO product (product_id, category_id, name, description, price, enabled) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                uuidPersistenceTransformer.fromString(productDto.getProductId()),
                uuidPersistenceTransformer.fromString(productDto.getCategoryId()),
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                productDto.isEnabled()
        );
        return productDto;
    }


    protected ProductCategoryDto insertACategory() {
        ProductCategoryDto categoryDto = new ProductCategoryDto(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                true
        );

        String sql = "INSERT INTO product_categories (category_id, name, enabled) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                uuidPersistenceTransformer.fromString(categoryDto.getProductCategoryId()),
                categoryDto.getProductCategoryName(),
                categoryDto.isEnabled());
        return categoryDto;
    }
}
