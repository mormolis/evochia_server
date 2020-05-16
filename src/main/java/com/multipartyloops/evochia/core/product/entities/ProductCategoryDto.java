package com.multipartyloops.evochia.core.product.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDto {
    private String productCategoryId;
    private String productCategoryName;
    private boolean enabled;
}
