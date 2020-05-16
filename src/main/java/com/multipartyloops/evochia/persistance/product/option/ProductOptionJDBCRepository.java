package com.multipartyloops.evochia.persistance.product.option;

import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.product.option.ProductOptionSQLStatements.*;

@Repository
public class ProductOptionJDBCRepository implements ProductOptionRepository<ProductOptionDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    public ProductOptionJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
    }

    @Override
    public Optional<ProductOptionDto> getProductOptionById(String productOptionId) {
        Object binaryProductOptionId = uuidPersistenceTransformer.fromString(productOptionId);
        List<ProductOptionDto> query = jdbcTemplate.query(PRODUCT_OPTIONS_SELECT_BY_ID, this::parseOption, binaryProductOptionId);
        if (query.size() == 1) {
            return Optional.of(query.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<ProductOptionDto> getOptionsByProductId(String productId) {
        Object binaryProductId = uuidPersistenceTransformer.fromString(productId);
        return jdbcTemplate.query(PRODUCT_OPTIONS_SELECT_BY_PRODUCT_ID, this::parseOption, binaryProductId);
    }

    @Override
    public void insertOption(ProductOptionDto productOptionDto) {

        jdbcTemplate.update(
                PRODUCT_OPTIONS_INSERTION,
                uuidPersistenceTransformer.fromString(productOptionDto.getProductOptionId()),
                uuidPersistenceTransformer.fromString(productOptionDto.getProductId()),
                productOptionDto.getVariation(),
                productOptionDto.getPrice()
        );

    }

    @Override
    public void deleteOptionById(String productOptionId) {

        Object binaryProductOptionId = uuidPersistenceTransformer.fromString(productOptionId);
        jdbcTemplate.update(
                PRODUCT_OPTIONS_DELETE_BY_OPTION_ID,
                binaryProductOptionId
        );
    }

    @Override
    public void deleteAllOptionsOfAProduct(String productId) {
        Object binaryProductId = uuidPersistenceTransformer.fromString(productId);
        jdbcTemplate.update(
                PRODUCT_OPTION_DELETE_ALL_BY_PRODUCT_ID,
                binaryProductId
        );
    }

    @Override
    public void updateProductOption(String variation, BigDecimal price, String productOptionId) {

        jdbcTemplate.update(
                "UPDATE product_options SET variation=?, price=? WHERE option_id=?",
                variation,
                price,
                uuidPersistenceTransformer.fromString(productOptionId)
        );
    }

    private ProductOptionDto parseOption(ResultSet resultSet, int _i) throws SQLException {
        return new ProductOptionDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("option_id")),
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("product_id")),
                resultSet.getString("variation"),
                resultSet.getBigDecimal("price")
        );
    }
}
