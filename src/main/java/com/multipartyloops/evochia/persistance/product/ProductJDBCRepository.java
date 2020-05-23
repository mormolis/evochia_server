package com.multipartyloops.evochia.persistance.product;

import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.multipartyloops.evochia.persistance.product.ProductSQLStatements.*;

@Repository
public class ProductJDBCRepository implements ProductRepository<ProductDto> {

    private final UuidPersistenceTransformer uuidPersistenceTransformer;
    private final JdbcTemplate jdbcTemplate;
    private final ProductOptionRepository<ProductOptionDto> productOptionRepository;

    public ProductJDBCRepository(UuidPersistenceTransformer uuidPersistenceTransformer, JdbcTemplate jdbcTemplate, ProductOptionRepository<ProductOptionDto> productOptionRepository) {
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
        this.jdbcTemplate = jdbcTemplate;
        this.productOptionRepository = productOptionRepository;
    }

    @Override
    public void insertProduct(ProductDto product) {

        jdbcTemplate.update(PRODUCT_INSERTION,
                uuidPersistenceTransformer.fromString(product.getProductId()),
                uuidPersistenceTransformer.fromString(product.getCategoryId()),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isEnabled()
        );

        product.getProductOptions()
                .forEach(productOptionRepository::insertOption);

    }

    @Override
    public void deleteProduct(String productId) {
        Object binaryProductId = uuidPersistenceTransformer.fromString(productId);
        productOptionRepository.deleteAllOptionsOfAProduct(productId);
        jdbcTemplate.update(PRODUCT_DELETE_BY_ID, binaryProductId);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<ProductDto> products = jdbcTemplate.query(PRODUCTS_SELECT_ALL, this::parseProductWithoutOptions);
        populateProductsWithOptions(products);
        return products;
    }

    @Override
    public List<ProductDto> getProductsByCategory(String productCategoryId) {

        Object binaryProductCategoryId = uuidPersistenceTransformer.fromString(productCategoryId);
        List<ProductDto> productsByCategory = jdbcTemplate.query(
                PRODUCT_SELECT_ALL_BY_CATEGORY,
                this::parseProductWithoutOptions,
                binaryProductCategoryId);

        populateProductsWithOptions(productsByCategory);
        return productsByCategory;
    }

    @Override
    public List<ProductDto> getEnabledProductsByCategory(String productCategoryId) {
        Object binaryProductCategoryId = uuidPersistenceTransformer.fromString(productCategoryId);
        List<ProductDto> enabledProductsByCategory = jdbcTemplate.query(
                PRODUCT_SELECT_ALL_ENABLED_BY_CATEGORY,
                this::parseProductWithoutOptions,
                binaryProductCategoryId, true);

        populateProductsWithOptions(enabledProductsByCategory);
        return enabledProductsByCategory;
    }

    @Override
    public Optional<ProductDto> getProductById(String productId) {
        Object binaryProductId = uuidPersistenceTransformer.fromString(productId);

        List<ProductDto> productById = jdbcTemplate.query(
                PRODUCT_SELECT_BY_PRODUCT_ID,
                this::parseProductWithoutOptions,
                binaryProductId);

        if (productById.size() == 1) {
            List<ProductOptionDto> optionsByProductId = productOptionRepository.getOptionsByProductId(productId);
            ProductDto productDto = productById.get(0);
            productDto.setProductOptions(optionsByProductId);
            return Optional.of(productDto);
        }

        return Optional.empty();
    }

    @Override
    public void updateProduct(ProductDto product) {
        jdbcTemplate.update(
                PRODUCT_UPDATE,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.isEnabled(),
                uuidPersistenceTransformer.fromString(product.getProductId())
        );
    }

    @Override
    public void updateProductsCategory(String productId, String newCategoryId) {

        jdbcTemplate.update(PRODUCT_CATEGORY_UPDATE,
                uuidPersistenceTransformer.fromString(newCategoryId),
                uuidPersistenceTransformer.fromString(productId)
        );
    }

    private ProductDto parseProductWithoutOptions(ResultSet resultSet, int _i) throws SQLException {
        return new ProductDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("product_id")),
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("category_id")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getBoolean("enabled"),
                null
        );
    }

    private void populateProductsWithOptions(List<ProductDto> productsWithoutOptions) {
        productsWithoutOptions.forEach(product -> {
            List<ProductOptionDto> optionsByProductId = productOptionRepository.getOptionsByProductId(product.getProductId());
            product.setProductOptions(optionsByProductId);
        });
    }
}
