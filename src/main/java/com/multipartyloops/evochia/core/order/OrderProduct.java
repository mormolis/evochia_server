package com.multipartyloops.evochia.core.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class OrderProduct {


    @JsonProperty("id")
    private final String orderProductId;
    @JsonProperty("productId")
    private final String productId;
    @JsonProperty("options")
    private final List<ProductOptionDto> options;
    @JsonProperty("discountPercentage")
    private final int discountPercentage;
    @JsonProperty("notes")
    private String notes;
    @JsonProperty("paid")
    private boolean paid;
    @JsonProperty("terminalId")
    private final String terminalId;
    @JsonProperty("productName")
    private final String productName;
    @JsonProperty("price")
    private final BigDecimal price;
    @JsonProperty("canceled")
    private boolean canceled;

    public OrderProduct(@JsonProperty("id") String orderProductId,
                        @JsonProperty("productId") String productId,
                        @JsonProperty("options") List<ProductOptionDto> options,
                        @JsonProperty("discountPercentage") int discountPercentage,
                        @JsonProperty("notes") String notes,
                        @JsonProperty("paid") boolean paid,
                        @JsonProperty("terminalId") String terminalId,
                        @JsonProperty("productName") String productName,
                        @JsonProperty("price") BigDecimal price,
                        @JsonProperty("canceled") boolean canceled) {
        this.orderProductId = orderProductId;
        this.productId = productId;
        this.options = options;
        discountValidator(discountPercentage);
        this.discountPercentage = discountPercentage;
        this.notes = notes;
        this.paid = paid;
        this.terminalId = terminalId;
        this.productName = productName;
        this.price = price;
        this.canceled = canceled;
    }

    public synchronized BigDecimal calculateTotalPrice() {
        if (canceled || paid) {
            return BigDecimal.ZERO;
        }

        var optionsPrice = additionalProductOptionsPrice();
        var productPrice = price.add(optionsPrice);
        var productDiscount = productPrice.multiply(new BigDecimal(calculateDiscountPercentage()));
        return productPrice.subtract(productDiscount);
    }

    public synchronized void setPaid(boolean paid) {
        this.paid = paid;
    }

    public synchronized void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    private BigDecimal additionalProductOptionsPrice() {
        return options
                .stream()
                .map(ProductOptionDto::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    String calculateDiscountPercentage() {
        if (discountPercentage < 10 && discountPercentage >= 0) {
            return "0.0" + discountPercentage;
        }
        if (discountPercentage >= 10 && discountPercentage < 100) {
            return "0." + discountPercentage;
        }
        return "1"; // discount == 100
    }

    private void discountValidator(int discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage needs to be between 0 and 100");
        }
    }
}
