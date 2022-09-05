package com.multipartyloops.evochia.core.order.dtos;

import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoDto {
    private String orderId;
    private String tableId;
    private String userId;
    private Boolean active;
    private Boolean canceled;
    private LocalDateTime lastUpdated;
    private String comments;
    private OrderDetails details;
}
