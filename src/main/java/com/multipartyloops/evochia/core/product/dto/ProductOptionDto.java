package com.multipartyloops.evochia.core.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductOptionDto {

    private String productOptionId;
    private String productId;
    private String variation;
    private BigDecimal price;
}
