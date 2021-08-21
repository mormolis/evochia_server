package com.multipartyloops.evochia.entrypoints.product.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductCategoryRequestBody {
    private String productCategoryId;
}
