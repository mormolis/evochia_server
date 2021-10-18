package com.multipartyloops.evochia.core.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;
import com.multipartyloops.evochia.core.product.dto.ProductDto;
import com.multipartyloops.evochia.core.product.dto.ProductOptionDto;
import com.multipartyloops.evochia.entrypoints.order.dtos.NewOrder;
import com.multipartyloops.evochia.persistance.order.OrderRepository;
import com.multipartyloops.evochia.persistance.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    public static final String A_TABLE_ID = UUID.randomUUID().toString();
    private static final String A_PRODUCT_ID = UUID.randomUUID().toString();
    private static final String ANOTHER_PRODUCT_ID = UUID.randomUUID().toString();
    private static final String A_USER_ID = UUID.randomUUID().toString();
    private static final String A_PRODUCT_OPTION_ID = UUID.randomUUID().toString();
    private static final String ANOTHER_PRODUCT_OPTION_ID = UUID.randomUUID().toString();
    private static final String A_TERMINAL_ID = UUID.randomUUID().toString();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mock
    private OrderRepository<OrderInfoDto> orderRepositoryMock;
    @Mock
    private ProductRepository<ProductDto> productRepository;
    @Mock
    private OrderInfoDto orderInfoDtoMock;

    @Captor
    private ArgumentCaptor<String> captor;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepositoryMock, productRepository, OBJECT_MAPPER);
    }

    @Test
    void addsANewOrderToRepository() {
        NewOrder newOrder = new NewOrder(A_TABLE_ID, A_USER_ID, "aComment",
                                         List.of(new NewOrder.NewOrderDetails(A_PRODUCT_ID,
                                                                              List.of(A_PRODUCT_OPTION_ID),
                                                                              A_TERMINAL_ID,
                                                                              10,
                                                                              "aNote"),
                                                 new NewOrder.NewOrderDetails(ANOTHER_PRODUCT_ID,
                                                                              List.of(),
                                                                              A_TERMINAL_ID,
                                                                              10,
                                                                              "aNote")));


        given(orderRepositoryMock.addNewOrder(
                eq(A_TABLE_ID),
                eq(A_USER_ID),
                eq("aComment"),
                any(String.class))).willReturn(orderInfoDtoMock);
        given(productRepository.getProductById(A_PRODUCT_ID)).willReturn(Optional.of(new ProductDto(A_PRODUCT_ID, "aCategoryId", "aName", "aDescription", new BigDecimal(10), true, A_TERMINAL_ID, List.of(new ProductOptionDto(A_PRODUCT_OPTION_ID, A_PRODUCT_ID, "aVariation", BigDecimal.ZERO), new ProductOptionDto(ANOTHER_PRODUCT_OPTION_ID, A_PRODUCT_ID, "anotherVariation", BigDecimal.ZERO)))));
        given(productRepository.getProductById(ANOTHER_PRODUCT_ID)).willReturn(Optional.of(new ProductDto(ANOTHER_PRODUCT_ID, "aCategoryId", "aName", "aDescription", new BigDecimal(10), true, A_TERMINAL_ID, List.of(new ProductOptionDto(A_PRODUCT_OPTION_ID, A_PRODUCT_ID, "aVariation", BigDecimal.ZERO)))));
        given(orderInfoDtoMock.getOrderId()).willReturn("an-id");

        final var orderId = orderService.addNewOrder(newOrder);

        then(orderRepositoryMock).should().addNewOrder(eq(A_TABLE_ID),
                                                       eq(A_USER_ID),
                                                       eq("aComment"),
                                                       captor.capture());
        final var detailsAsString = captor.getValue();
        assertThat(detailsAsString).contains("\"totalPrice\":18.00", "\"productId\":\"" + A_PRODUCT_ID + "\"", "\"productId\":\"" + ANOTHER_PRODUCT_ID + "\"");
        assertThat(orderId).isEqualTo("an-id");
    }

}