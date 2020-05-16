package com.multipartyloops.evochia.persistance.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository<T> {

    void insertProduct(T product);

    void deleteProduct(String productId);

    List<T> getAllProducts();

    List<T> getProductsByCategory(String productCategoryId);

    List<T> getEnabledProductsByCategory(String productCategoryId);

    Optional<T> getProductById(String productId);

    void updateProduct(T product);

}
