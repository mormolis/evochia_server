package com.multipartyloops.evochia.core.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrderDetails {

    @JsonProperty("orderProducts")
    private final List<OrderProduct> orderProducts;
    @JsonProperty("totalPrice")
    private BigDecimal billTotal;

    public OrderDetails() {
        this.orderProducts = new ArrayList<>();
        this.billTotal = BigDecimal.ZERO;
    }

    public synchronized void addToOrder(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        updateTotalPrice();
    }

    public synchronized BigDecimal getProductPriceFromOrder(String orderProductId) {
        return orderProducts.stream()
                .filter(orderProduct -> orderProduct.getOrderProductId().equals(orderProductId))
                .filter(orderProduct -> !orderProduct.isPaid())
                .filter(orderProduct -> !orderProduct.isCanceled())
                .map(OrderProduct::calculateTotalPrice)
                .findFirst().orElse(BigDecimal.ZERO);
    }

    public synchronized Optional<OrderProduct> removeFromOrder(String orderProductId) {
        final var orderProduct = findOrderProductById(orderProductId);

        orderProduct.ifPresent(entry -> {
            orderProducts.remove(entry);
            updateTotalPrice();
        });
        return orderProduct;
    }

    public synchronized boolean cancelOrderProductBy(String orderProductId) {
        final var changed = new AtomicBoolean(false);
        findOrderProductById(orderProductId)
                .filter(orderProduct -> !orderProduct.isCanceled())
                .ifPresent(order -> {
                    order.setCanceled(true);
                    changed.set(true);
                    updateTotalPrice();
                });
        return changed.get();
    }

    public synchronized BigDecimal getBillTotal() {
        updateTotalPrice();
        return billTotal;
    }

    public synchronized boolean markOrderProductAsPaid(String orderProductId) {
        return setPaidStatusForAnOrderProduct(orderProductId, true);
    }

    public synchronized boolean markProductOrderAsNotPaid(String orderProductId) {
        return setPaidStatusForAnOrderProduct(orderProductId, false);
    }

    public String toJsonString(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean setPaidStatusForAnOrderProduct(String orderProductId, boolean isItPaid) {
        final var changed = new AtomicBoolean(false);
        findOrderProductById(orderProductId)
                .filter(orderProduct -> !orderProduct.isCanceled())
                .filter(orderProduct -> orderProduct.isPaid() != isItPaid)
                .ifPresent(orderProduct -> {
                    orderProduct.setPaid(isItPaid);
                    updateTotalPrice();
                    changed.set(true);
                });
        return changed.get();
    }

    private Optional<OrderProduct> findOrderProductById(String orderProductId) {
        return orderProducts.stream()
                .filter(order -> order.getOrderProductId().equals(orderProductId))
                .findFirst();
    }

    private void updateTotalPrice() {
        billTotal = orderProducts.stream()
                .filter(orderProduct -> !orderProduct.isPaid())
                .filter(orderProduct -> !orderProduct.isCanceled())
                .map(OrderProduct::calculateTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDetails)) return false;
        OrderDetails that = (OrderDetails) o;
        return Objects.equals(orderProducts, that.orderProducts) && Objects.equals(billTotal, that.billTotal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderProducts, billTotal);
    }
}
