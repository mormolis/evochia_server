package com.multipartyloops.evochia.entrypoints.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryChangeRequestBody {

    @JsonProperty
    private String productId;

    @JsonProperty
    private String destinationCategoryId;
}
