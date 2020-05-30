package com.multipartyloops.evochia.core.product;

import com.multipartyloops.evochia.core.commons.UUIDFormatChecker;
import com.multipartyloops.evochia.core.product.entities.ProductDto;
import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;
import com.multipartyloops.evochia.core.product.exceptions.CategoryDoesNotExistException;
import com.multipartyloops.evochia.core.product.exceptions.MandatoryFieldNotPassedException;
import com.multipartyloops.evochia.core.product.exceptions.ProductNotFoundException;
import com.multipartyloops.evochia.persistance.product.ProductRepository;
import com.multipartyloops.evochia.persistance.product.option.ProductOptionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository<ProductDto> productRepository;
    private final ProductOptionRepository<ProductOptionDto> productOptionRepository;

    public ProductService(ProductRepository<ProductDto> productRepository, ProductOptionRepository<ProductOptionDto> productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    public ProductDto addProduct(ProductDto productDto) {

        checkProductsMandatoryFieldsHaveBeenPassed(productDto);

        final String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);

        populateProductOptionsIdValues(productDto, productId);

        productRepository.insertProduct(productDto);
        return productDto;
    }

    public void deleteProduct(String productId) {

        UUIDFormatChecker.confirmOrThrow(productId, new ProductNotFoundException("Product not found"));
        productOptionRepository.deleteAllOptionsOfAProduct(productId);
        productRepository.deleteProduct(productId);
    }

    public List<ProductDto> getAllProductsByCategory(String categoryId) {

        UUIDFormatChecker.confirmOrThrow(categoryId, new CategoryDoesNotExistException("Category Id does not exist"));
        return productRepository.getProductsByCategory(categoryId);
    }

    public List<ProductDto> getAllProducts() {

        return productRepository.getAllProducts();
    }

    public List<ProductDto> getEnabledProductsByCategory(String categoryId) {

        try {
            return productRepository.getEnabledProductsByCategory(categoryId);
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    public ProductDto getProductById(String productId) {

        UUIDFormatChecker.confirmOrThrow(productId, new ProductNotFoundException("Product not found"));

        return productRepository.getProductById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product not found")
        );
    }

    public void updateProduct(String productId, String name, String description, BigDecimal price, Boolean enabled, List<ProductOptionDto> productOptions) {

        checkProductIdIsPassed(productId);
        UUIDFormatChecker.confirmOrThrow(productId, new ProductNotFoundException("Product not found"));

        ProductDto existingProduct = getProductById(productId);
        ProductDto updatedProduct = constructUpdatedProduct(existingProduct, name, description, price, enabled);

        updateProductOptions(productId, productOptions);

        productRepository.updateProduct(updatedProduct);
    }

    public void changeCategoryOfAProduct(String productId, String toCategoryId) {
        UUIDFormatChecker.confirmOrThrow(productId, new IllegalArgumentException("Product not found"));
        UUIDFormatChecker.confirmOrThrow(toCategoryId, new IllegalArgumentException("Category Id does not exist"));

        try {
            productRepository.updateProductsCategory(productId, toCategoryId);
        }catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Product or/and category do not exist");
        }
    }

    private void updateProductOptions(String productId, List<ProductOptionDto> productOptions) {
        if (productOptions != null) {
            productOptionRepository.deleteAllOptionsOfAProduct(productId);
            productOptions.forEach(option -> {
                option.setProductOptionId(UUID.randomUUID().toString());
                option.setProductId(productId);
                productOptionRepository.insertOption(option);
            });
        }
    }

    private void checkProductsMandatoryFieldsHaveBeenPassed(ProductDto productDto) {
        if (productDto.getCategoryId() == null || "".equals(productDto.getCategoryId())) {
            throw new MandatoryFieldNotPassedException("Product needs to have a category");
        }
        if (productDto.getName() == null || "".equals(productDto.getName())) {
            throw new MandatoryFieldNotPassedException("Product needs to have a name");
        }

        if (productDto.getPrice() == null) {
            throw new MandatoryFieldNotPassedException("Product needs to have a price");
        }
    }

    private ProductDto constructUpdatedProduct(ProductDto existingProduct, String name, String description, BigDecimal price, Boolean enabled) {
        return new ProductDto(
                existingProduct.getProductId(),
                existingProduct.getCategoryId(),
                name != null ? name : existingProduct.getName(),
                description != null ? description : existingProduct.getDescription(),
                price != null ? price : existingProduct.getPrice(),
                enabled != null ? enabled : existingProduct.isEnabled(),
                null
        );
    }

    private void checkProductIdIsPassed(String productId) {
        if (productId == null || "".equals(productId)) {
            throw new MandatoryFieldNotPassedException("Cannot update product without productId");
        }
    }

    private void populateProductOptionsIdValues(ProductDto productDto, String productId) {
        productDto.getProductOptions().forEach(option -> {
            option.setProductId(productId);
            option.setProductOptionId(UUID.randomUUID().toString());
        });
    }
}
