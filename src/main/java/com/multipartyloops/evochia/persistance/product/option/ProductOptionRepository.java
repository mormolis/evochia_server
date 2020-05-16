package com.multipartyloops.evochia.persistance.product.option;

import com.multipartyloops.evochia.core.product.entities.ProductOptionDto;

import java.util.List;
import java.util.Optional;

public interface ProductOptionRepository<T> {

    Optional<T> getProductOptionById(String productOptionId);

    List<ProductOptionDto> getOptionsByProductId(String productId);

    void insertOption(ProductOptionDto productOptionDto);

    void deleteOptionById(String productOptionId);

    void deleteAllOptionsOfAProduct(String productId);

}
