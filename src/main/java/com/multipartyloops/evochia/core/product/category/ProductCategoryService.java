package com.multipartyloops.evochia.core.product.category;

import com.multipartyloops.evochia.core.product.entities.ProductCategoryDto;
import com.multipartyloops.evochia.core.product.exceptions.InvalidCategoryNameException;
import com.multipartyloops.evochia.core.product.exceptions.InvalidProductCategoryId;
import com.multipartyloops.evochia.core.product.exceptions.ProductCategoryCouldNotBeFoundException;
import com.multipartyloops.evochia.persistance.product.category.ProductCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCategoryService {


    private final ProductCategoryRepository<ProductCategoryDto> productCategoryRepository;

    Comparator<ProductCategoryDto> enabledFirst = (a, b) -> Boolean.compare(b.isEnabled(), a.isEnabled());
    Comparator<ProductCategoryDto> sortByName = Comparator.comparing(ProductCategoryDto::getProductCategoryName);

    public ProductCategoryService(ProductCategoryRepository<ProductCategoryDto> productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    public ProductCategoryDto getById(String productCategoryId) {
        checkIdNullOrEmpty(productCategoryId);
        return productCategoryRepository.getProductCategoryById(productCategoryId)
                .orElseThrow(() -> new ProductCategoryCouldNotBeFoundException("Product category not found"));
    }

    public List<ProductCategoryDto> getAll() {
        List<ProductCategoryDto> allProductCategories = productCategoryRepository.getAllProductCategories();
        allProductCategories.sort(enabledFirst.thenComparing(sortByName));
        return allProductCategories;
    }

    public List<ProductCategoryDto> getAllEnabledCategories() {
        List<ProductCategoryDto> allEnabledProductCategories = productCategoryRepository.getAllEnabledProductCategories();
        allEnabledProductCategories.sort(sortByName);
        return allEnabledProductCategories;
    }

    public void update(String productCategoryId, String updatedName, boolean updatedEnabledValue) {
        checkIdNullOrEmpty(productCategoryId);
        productCategoryRepository.updateProductCategory(new ProductCategoryDto(productCategoryId, updatedName, updatedEnabledValue));
    }

    public void delete(String productCategoryId) {
        checkIdNullOrEmpty(productCategoryId);
        productCategoryRepository.deleteProductCategoryById(productCategoryId);
    }

    public void enableById(String productCategoryId) {
        Optional<ProductCategoryDto> productCategoryById = productCategoryRepository.getProductCategoryById(productCategoryId);
        productCategoryById.ifPresent((category) -> {
            if (!category.isEnabled()) {
                productCategoryRepository.updateProductCategory(new ProductCategoryDto(productCategoryId, category.getProductCategoryName(), true));
            }
        });
    }

    public void disableById(String productCategoryId) {
        Optional<ProductCategoryDto> productCategoryById = productCategoryRepository.getProductCategoryById(productCategoryId);
        productCategoryById.ifPresent((category) -> {
            if (category.isEnabled()) {
                productCategoryRepository.updateProductCategory(new ProductCategoryDto(productCategoryId, category.getProductCategoryName(), false));
            }
        });
    }

    public String addNewCategory(ProductCategoryDto categoryToAdd) {
        String productCategoryId = UUID.randomUUID().toString();
        categoryToAdd.setProductCategoryId(productCategoryId);
        checkCategoryNameIsNotEmptyOrNull(categoryToAdd);
        productCategoryRepository.addProductCategory(categoryToAdd);
        return productCategoryId;
    }

    private void checkCategoryNameIsNotEmptyOrNull(ProductCategoryDto categoryToAdd) {
        if (categoryToAdd.getProductCategoryName() == null || categoryToAdd.getProductCategoryName().isEmpty()) {
            throw new InvalidCategoryNameException("Category name should be provided");
        }
    }

    private void checkIdNullOrEmpty(String productCategoryId) {
        if (productCategoryId == null || "".equals(productCategoryId)) {
            throw new InvalidProductCategoryId("Error related to the product category id provided");
        }
    }
}
