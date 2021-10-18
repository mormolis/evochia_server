package com.multipartyloops.evochia.entrypoints.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewOrderResponse {

    @JsonProperty("orderId")
    private String orderId;
}
