package com.multipartyloops.evochia.persistance.product.option;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository<T> {

    Optional<T> getProductOptionById(String productOptionId);

    List<T> getOptionsByProductId(String productId);

    void insertOption(T productOptionDto);

    void deleteOptionById(String productOptionId);

    void deleteAllOptionsOfAProduct(String productId);

    void updateProductOption(String variation, BigDecimal price, String productOptionId);

}
