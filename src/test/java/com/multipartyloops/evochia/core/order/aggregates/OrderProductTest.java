package com.multipartyloops.evochia.core.order.aggregates;

import com.multipartyloops.evochia.core.order.aggregates.OrderProduct;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class OrderProductTest {

    @ParameterizedTest
    @CsvSource({"1,0.01", "100,1", "34,0.34", "0,0.00"})
    void discountPercentage(int input, String output) {
        assertThat(anOrderProductWith(input).calculateDiscountPercentage()).isEqualTo(output);
    }

    @ParameterizedTest
    @CsvSource({"-10", "101"})
    void discountPercentageCalculationThrowsWhenInputIsInvalid(int input) {
        assertThatThrownBy(() -> anOrderProductWith(input))
                .isInstanceOf(IllegalArgumentException.class);
    }


    private OrderProduct anOrderProductWith(int discountPercentage) {
        return new OrderProduct("a-unique-order-product-id",
                "anId",
                List.of(),
                discountPercentage,
                "anote",
                false,
                "a-terminal-id",
                "aproduct",
                new BigDecimal("2.5"),
                false);
    }

}