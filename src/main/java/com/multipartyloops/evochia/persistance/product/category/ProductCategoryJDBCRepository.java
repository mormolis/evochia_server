package com.multipartyloops.evochia.persistance.product.category;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.product.ProductRepository;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.product.category.ProductCategorySQLStatements.*;

@Repository
public class ProductCategoryJDBCRepository implements ProductCategoryRepository<ProductCategoryDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;

    private final ProductRepository<ProductDto> productRepository;
    private final ProductOptionRepository<ProductOptionDto> productOptionRepository;

    public ProductCategoryJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer, ProductRepository<ProductDto> productRepository, ProductOptionRepository<ProductOptionDto> productOptionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    @Override
    public List<ProductCategoryDto> getAllProductCategories() {
        return jdbcTemplate.query(PRODUCT_CATEGORIES_SELECT_ALL, this::parseProductCategory);
    }

    @Override
    public List<ProductCategoryDto> getAllEnabledProductCategories() {
        return jdbcTemplate.query(PRODUCT_CATEGORIES_SELECT_ALL_BY_ENABLED, this::parseProductCategory, true);
    }

    @Override
    public List<ProductCategoryDto> getAllDisabledProductCategories() {
        return jdbcTemplate.query(PRODUCT_CATEGORIES_SELECT_ALL_BY_ENABLED, this::parseProductCategory, false);
    }

    @Override
    public Optional<ProductCategoryDto> getProductCategoryById(String productCategoryId) {
        Object binaryProductCategoryId = uuidPersistenceTransformer.fromString(productCategoryId);
        List<ProductCategoryDto> query = jdbcTemplate.query(PRODUCT_CATEGORIES_SELECT_BY_CATEGORY_ID, this::parseProductCategory, binaryProductCategoryId);
        if (query.size() == 1) {
            return Optional.of(query.get(0));
        }
        return Optional.empty();
    }

    @Override
    public void addProductCategory(ProductCategoryDto productCategoryDto) {
        Object binaryProductCategoryId = uuidPersistenceTransformer.fromString(productCategoryDto.getProductCategoryId());

        jdbcTemplate.update(
                PRODUCT_CATEGORIES_INSERTION,
                binaryProductCategoryId,
                productCategoryDto.getProductCategoryName(),
                productCategoryDto.isEnabled()
        );
    }

    @Override
    public void deleteProductCategoryById(String productCategoryId) {

        List<ProductDto> productsByCategory = productRepository.getProductsByCategory(productCategoryId);
        productsByCategory.forEach(product -> productOptionRepository.deleteAllOptionsOfAProduct(product.getProductId()));
        productsByCategory.forEach(product -> productRepository.deleteProduct(product.getProductId()));

        Object binaryProductCategoryId = uuidPersistenceTransformer.fromString(productCategoryId);

        jdbcTemplate.update(PRODUCT_CATEGORIES_DELETE_CATEGORY, binaryProductCategoryId);
    }

    private ProductCategoryDto parseProductCategory(ResultSet resultSet, int i) throws SQLException {
        return new ProductCategoryDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("category_id")),
                resultSet.getString("name"),
                resultSet.getBoolean("enabled")
        );
    }
}
