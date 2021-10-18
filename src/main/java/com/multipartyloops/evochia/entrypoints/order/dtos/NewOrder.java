package com.multipartyloops.evochia.entrypoints.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewOrder {

    @JsonProperty("tableId")
    private String tableId;
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("details")
    private List<NewOrderDetails> details;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewOrderDetails {
        @JsonProperty("productId")
        private String productId;
        @JsonProperty("options")
        private List<String> options;
        @JsonProperty("terminalId")
        private String terminalId;
        @JsonProperty("discountPercentage")
        private int discountPercentage;
        @JsonProperty("notes")
        private String notes;
    }
}
