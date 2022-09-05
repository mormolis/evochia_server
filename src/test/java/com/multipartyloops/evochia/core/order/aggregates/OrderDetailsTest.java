package com.multipartyloops.evochia.core.order.aggregates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import com.multipartyloops.evochia.core.order.aggregates.OrderProduct;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderDetailsTest {

    private OrderDetails orderDetails;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        orderDetails = new OrderDetails();
    }

    final List<ProductOptionDto> productOptions = List.of(new ProductOptionDto("an-id", "a-product-id", "aVariation", new BigDecimal("0.5")),
            new ProductOptionDto("an-id", "a-product-id", "aVariation", new BigDecimal(1)));
    OrderProduct orderProduct = new OrderProduct("a-unique-order-product-id",
            "anId",
            productOptions,
            10,
            "anote",
            false,
            "a-terminal-id",
            "aproduct",
            new BigDecimal("2.5"),
            false);
    OrderProduct anotherProduct = new OrderProduct("another-unique-order-product-id",
            "anId",
            productOptions,
            10,
            "anote",
            false,
            "a-terminal-id",
            "aproduct",
            new BigDecimal(5),
            false);

    OrderProduct aPaidOrderProduct = new OrderProduct("a-paid-one",
            "anId",
            productOptions,
            10,
            "anote",
            true,
            "a-terminal-id",
            "aproduct",
            new BigDecimal(5),
            false);
    OrderProduct aCanceledOrderProduct = new OrderProduct("a-canceled-one",
            "anId",
            productOptions,
            10,
            "anote",
            false,
            "a-terminal-id",
            "aproduct",
            new BigDecimal(100),
            true);
    OrderProduct aNoDiscountOrderProduct = new OrderProduct("a-canceled-one",
            "anId",
            productOptions,
            0,
            "anote",
            false,
            "a-terminal-id",
            "aproduct",
            new BigDecimal(100),
            false);

    @Test
    void calculatesTheFinalPrice() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);
        orderDetails.addToOrder(aPaidOrderProduct);
        orderDetails.addToOrder(aCanceledOrderProduct);

        final var totalPrice = orderDetails.getBillTotal();

        assertThat(totalPrice).isEqualTo(new BigDecimal("9.45"));
    }

    @Test
    void calculatesTheFinalPriceForNonDiscounted() {
        orderDetails.addToOrder(aNoDiscountOrderProduct);

        final var totalPrice = orderDetails.getBillTotal();

        assertThat(totalPrice).isEqualTo(new BigDecimal("101.50"));
    }

    @Test
    void returnsAJsonStringForTheOrder() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);

        assertThat(orderDetails.toJsonString(objectMapper)).isEqualTo("{\"orderProducts\":[{\"id\":\"a-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}");
    }

    @Test
    void jsonStringCanBeConvertedToOrderDetailsObject() throws JsonProcessingException {
        String jsonString = "{\"orderProducts\":[{\"id\":\"a-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}";
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);

        final var generated = objectMapper.readValue(jsonString, OrderDetails.class);

        assertThat(generated).isEqualTo(orderDetails);
    }

    @Test
    void changesToGeneratedFromJsonObjectDoesNotAffectTheRealOrder() throws JsonProcessingException {
        String jsonString = "{\"orderProducts\":[{\"id\":\"a-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}";
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);

        final var generated = objectMapper.readValue(jsonString, OrderDetails.class);
        generated.markOrderProductAsPaid("a-unique-order-product-id");

        assertThat(generated).isNotEqualTo(orderDetails);
    }

    @Test
    void whenAProductIsRemovedFromTheListThePriceIsCalculated() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);

        final var orderProduct = orderDetails.removeFromOrder("another-unique-order-product-id");

        assertThat(orderProduct.get()).isEqualTo(anotherProduct);
        assertThat(orderDetails.getBillTotal()).isEqualTo(new BigDecimal("3.60"));
    }

    @Test
    void whenAProductForRemovalDoesNotExistTheBillRemainsTheSame() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);
        orderDetails.addToOrder(aPaidOrderProduct);

        final var orderProduct = orderDetails.removeFromOrder("booohh!!");

        assertThat(orderProduct).isEmpty();
        assertThat(orderDetails.getBillTotal()).isEqualTo(new BigDecimal("9.45"));
    }

    @Test
    void whenAPaidProductIsRemovedFromTheListTheBillIsNotAffected() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);
        orderDetails.addToOrder(aPaidOrderProduct);

        final var orderProduct = orderDetails.removeFromOrder("a-paid-one");

        assertThat(orderProduct.get()).isEqualTo(aPaidOrderProduct);
        assertThat(orderDetails.getBillTotal()).isEqualTo(new BigDecimal("9.45"));
        assertThat(orderDetails.removeFromOrder("a-paid-one")).isEmpty();
    }

    @Test
    void whenAProductOrderIsCanceledTheBillGetsUpdated() {
        orderDetails.addToOrder(orderProduct);
        orderDetails.addToOrder(anotherProduct);
        orderDetails.addToOrder(aPaidOrderProduct);

        assertThat(orderDetails.cancelOrderProductBy("a-unique-order-product-id")).isTrue();
        assertThat(orderDetails.getBillTotal()).isEqualTo(new BigDecimal("5.85"));
    }

    @Test
    void anOrderProductThatIsCanceledCannotBeCanceledAgain() {
        orderDetails.addToOrder(aCanceledOrderProduct);

        assertThat(orderDetails.cancelOrderProductBy(aCanceledOrderProduct.getOrderProductId())).isFalse();
    }

    @Test
    void anOrderProductThatIsPaidCannotBePaidAgain() {
        orderDetails.addToOrder(aPaidOrderProduct);

        assertThat(orderDetails.markOrderProductAsPaid(aPaidOrderProduct.getOrderProductId())).isFalse();
    }

    @Test
    void anUnpaidProductThatCannotBeMarkedAsUnpaid() {
        orderDetails.addToOrder(anotherProduct);

        assertThat(orderDetails.markProductOrderAsNotPaid(anotherProduct.getOrderProductId())).isFalse();
    }


    @Test
    void anOrderProductCanBeMarkedAsPaid() {
        orderDetails.addToOrder(orderProduct);

        assertThat(orderDetails.markOrderProductAsPaid("a-unique-order-product-id")).isTrue();

        assertThat(orderDetails.getBillTotal()).isEqualTo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void anOrderProductCanBeMarkedAsNotPaid() {
        orderDetails.addToOrder(aPaidOrderProduct);

        assertThat(orderDetails.markProductOrderAsNotPaid("a-paid-one")).isTrue();

        assertThat(orderDetails.getBillTotal()).isEqualTo(new BigDecimal("5.85"));
    }
}