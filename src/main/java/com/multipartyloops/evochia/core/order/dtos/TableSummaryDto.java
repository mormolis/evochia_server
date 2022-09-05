package com.multipartyloops.evochia.core.order.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableSummaryDto {

    @JsonProperty("orders")
    private List<OrderInfoDto> orders;
    @JsonProperty("tableTotal")
    private BigDecimal tableTotal;
}
