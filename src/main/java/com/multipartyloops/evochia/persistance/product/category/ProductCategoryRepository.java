package com.multipartyloops.evochia.persistance.product.category;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository<T> {

    List<T> getAllProductCategories();

    List<T> getAllEnabledProductCategories();

    List<T> getAllDisabledProductCategories();

    Optional<T> getProductCategoryById(String productCategoryId);

    void addProductCategory(T productCategoryDto);

    void deleteProductCategoryById(String productCategoryId);
}
