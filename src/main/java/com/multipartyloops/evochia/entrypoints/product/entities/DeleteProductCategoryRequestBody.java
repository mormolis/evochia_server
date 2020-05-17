package com.multipartyloops.evochia.entrypoints.product.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteProductCategoryRequestBody {
    private String productCategoryId;
}
