package com.multipartyloops.evochia.core.product.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("categoryId")
    private String categoryId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("preferredTerminalId")
    private String preferredTerminalId;

    @JsonProperty("productOptions")
    private List<ProductOptionDto> productOptions;

}
