package com.multipartyloops.evochia.entrypoints.product.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductUpdateRequestBody {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private BigDecimal price;

    @JsonProperty
    private boolean enabled;

    @JsonProperty
    private String preferredTerminalId;

    @JsonProperty
    private List<ProductOptionDto> productOptions;

}
