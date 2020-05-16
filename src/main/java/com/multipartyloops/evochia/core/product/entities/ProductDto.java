package com.multipartyloops.evochia.core.product.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    private String productId;
    private String categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean enabled;
    private List<ProductOptionDto> productOptions;

}
