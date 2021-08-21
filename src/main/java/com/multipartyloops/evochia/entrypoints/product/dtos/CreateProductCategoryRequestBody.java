package com.multipartyloops.evochia.entrypoints.product.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateProductCategoryRequestBody {

    @JsonProperty
    private String productCategoryName;

    @JsonProperty
    private boolean enabled;
}
