package com.multipartyloops.evochia.persistance.order;

import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;

import java.util.List;
import java.util.Optional;

public interface OrderRepository<T> {

    T addNewOrder(String tableId, String userId, String comments, OrderDetails details);

    Optional<T> getOrderById(String orderId);

    List<T> getActiveOrdersByTableId(String tableId);

    List<T> getActiveOrders();

    List<T> getAllOrders();

    void updateOrder(OrderInfoDto orderInfoDto);

    void cancelOrder(String orderId, String comments);

    void deleteOrder(String orderId);
}
