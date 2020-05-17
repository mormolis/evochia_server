package com.multipartyloops.evochia.entrypoints.product.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCategoryResponse {

    @JsonProperty
    private String productCategoryId;
}
