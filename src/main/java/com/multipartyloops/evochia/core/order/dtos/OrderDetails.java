package com.multipartyloops.evochia.core.order.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public void addToOrder(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        updateTotalPrice();
    }

    public Optional<OrderProduct> removeFromOrder(String orderProductId) {
        final var orderProduct = findOrderProductById(orderProductId);

        orderProduct.ifPresent(entry -> {
            orderProducts.remove(entry);
            updateTotalPrice();
        });
        return orderProduct;
    }

    public boolean cancelOrderProductBy(String orderProductId) {
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

    public BigDecimal getBillTotal() {
        return billTotal;
    }

    public boolean markOrderProductAsPaid(String orderProductId) {
        return setPaidStatusForAnOrderProduct(orderProductId, true);
    }

    public boolean markProductOrderAsNotPaid(String orderProductId) {
        return setPaidStatusForAnOrderProduct(orderProductId, false);
    }

    public String toJson(ObjectMapper objectMapper) {
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
                            .filter(order -> order.getId().equals(orderProductId))
                            .findFirst();
    }

    private void updateTotalPrice() {
        billTotal = orderProducts.stream()
                                 .filter(orderProduct -> !orderProduct.isPaid())
                                 .filter(orderProduct -> !orderProduct.isCanceled())
                                 .map(orderProduct -> {
                                     final var optionsPrice = additionalProductOptionsPrice(orderProduct);
                                     final var productPrice = orderProduct.getPrice().add(optionsPrice);
                                     final var productDiscount = productPrice.multiply(new BigDecimal(calculateDiscountPercentage(orderProduct.getDiscountPercentage())));
                                     return productPrice.subtract(productDiscount);
                                 }).reduce(BigDecimal.ZERO, BigDecimal::add)
                                 .setScale(2, RoundingMode.HALF_EVEN);
    }


    private BigDecimal additionalProductOptionsPrice(OrderProduct orderProduct) {
        return orderProduct.getOptions()
                           .stream()
                           .map(ProductOptionDto::getPrice)
                           .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    String calculateDiscountPercentage(int discountPercentage) {
        if (discountPercentage < 10 && discountPercentage >= 0) {
            return "0.0" + discountPercentage;
        }
        if (discountPercentage >= 10 && discountPercentage < 100) {
            return "0." + discountPercentage;
        }
        if (discountPercentage == 100) {
            return "1";
        }
        throw new IllegalArgumentException("Discount percentage needs to be between 0 and 100");
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderProduct {
        private String id;
        private String productId;
        private List<ProductOptionDto> options;
        private int discountPercentage;
        private String notes;
        private boolean paid;
        private String terminalId;
        private String productName;
        private BigDecimal price;
        private boolean canceled;
    }

}
