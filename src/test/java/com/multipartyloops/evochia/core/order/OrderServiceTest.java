package com.multipartyloops.evochia.core.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;
import com.multipartyloops.evochia.core.order.dtos.TableSummaryDto;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    public static final String A_TABLE_ID = UUID.randomUUID().toString();
    public static final String ANOTHER_TABLE_ID = UUID.randomUUID().toString();
    public static final String AN_ORDER_ID = UUID.randomUUID().toString();
    public static final String ANOTHER_ORDER_ID = UUID.randomUUID().toString();
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
    private ArgumentCaptor<OrderDetails> captor;

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
                any(OrderDetails.class))).willReturn(orderInfoDtoMock);
        given(productRepository.getProductById(A_PRODUCT_ID)).willReturn(Optional.of(new ProductDto(A_PRODUCT_ID, "aCategoryId", "aName", "aDescription", new BigDecimal(10), true, A_TERMINAL_ID, List.of(new ProductOptionDto(A_PRODUCT_OPTION_ID, A_PRODUCT_ID, "aVariation", BigDecimal.ZERO), new ProductOptionDto(ANOTHER_PRODUCT_OPTION_ID, A_PRODUCT_ID, "anotherVariation", BigDecimal.ZERO)))));
        given(productRepository.getProductById(ANOTHER_PRODUCT_ID)).willReturn(Optional.of(new ProductDto(ANOTHER_PRODUCT_ID, "aCategoryId", "aName", "aDescription", new BigDecimal(10), true, A_TERMINAL_ID, List.of(new ProductOptionDto(A_PRODUCT_OPTION_ID, A_PRODUCT_ID, "aVariation", BigDecimal.ZERO)))));
        given(orderInfoDtoMock.getOrderId()).willReturn("an-id");

        final var orderId = orderService.addNewOrder(newOrder);

        then(orderRepositoryMock).should().addNewOrder(eq(A_TABLE_ID),
                eq(A_USER_ID),
                eq("aComment"),
                captor.capture());
        final var detailsAsString = captor.getValue().toJsonString(OBJECT_MAPPER);
        assertThat(detailsAsString).contains("\"totalPrice\":18.00", "\"productId\":\"" + A_PRODUCT_ID + "\"", "\"productId\":\"" + ANOTHER_PRODUCT_ID + "\"");
        assertThat(orderId).isEqualTo("an-id");
    }

    @Test
    void getsAllActiveOrdersForATableAndSumsUpTheTotal() throws JsonProcessingException {
        var details = "{\"orderProducts\":[{\"id\":\"a-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}";
        var details2 = "{\"orderProducts\":[{\"id\":\"another-unique-order-product-id\",\"productId\":\"anotherId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}";
        List<OrderInfoDto> orders = List.of(new OrderInfoDto(AN_ORDER_ID, A_TABLE_ID, A_USER_ID, true, false, LocalDateTime.now(), "none", OBJECT_MAPPER.readValue(details, OrderDetails.class)), new OrderInfoDto(ANOTHER_ORDER_ID, A_TABLE_ID, A_USER_ID, true, false, LocalDateTime.now(), "none", OBJECT_MAPPER.readValue(details2, OrderDetails.class)));
        given(orderRepositoryMock.getActiveOrdersByTableId(A_TABLE_ID)).willReturn(orders);

        TableSummaryDto activeOrdersByTableId = orderService.getActiveOrdersByTableId(A_TABLE_ID);

        assertThat(activeOrdersByTableId.getTableTotal()).isEqualTo(new BigDecimal("18.90"));
        assertThat(activeOrdersByTableId.getOrders()).containsExactlyInAnyOrder(orders.get(0), orders.get(1));

    }

    @Test
    void sumsUpTheTotalFromDifferentTables() {
        OrderInfoDto orderInfoDto = mock(OrderInfoDto.class, RETURNS_DEEP_STUBS);
        OrderInfoDto orderInfoDto2 = mock(OrderInfoDto.class, RETURNS_DEEP_STUBS);
        given(orderRepositoryMock.getActiveOrdersByTableId(A_TABLE_ID)).willReturn(List.of(orderInfoDto));
        given(orderRepositoryMock.getActiveOrdersByTableId(ANOTHER_TABLE_ID)).willReturn(List.of(orderInfoDto2));
        given(orderInfoDto.getDetails().getProductPriceFromOrder(A_PRODUCT_ID)).willReturn(BigDecimal.ONE);
        given(orderInfoDto.getDetails().getProductPriceFromOrder(ANOTHER_PRODUCT_ID)).willReturn(BigDecimal.ZERO);
        given(orderInfoDto2.getDetails().getProductPriceFromOrder(A_PRODUCT_ID)).willReturn(BigDecimal.ZERO);
        given(orderInfoDto2.getDetails().getProductPriceFromOrder(ANOTHER_PRODUCT_ID)).willReturn(BigDecimal.ONE);

        BigDecimal totalOfProductsInDifferentTables = orderService.getTotalOfProductsInDifferentTables(List.of(A_TABLE_ID, ANOTHER_TABLE_ID), List.of(A_PRODUCT_ID, ANOTHER_PRODUCT_ID));

        assertThat(totalOfProductsInDifferentTables).isEqualTo(BigDecimal.valueOf(2.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void marksProductsAsPaid() {
        OrderInfoDto orderInfoDto = mock(OrderInfoDto.class, RETURNS_DEEP_STUBS);
        OrderInfoDto orderInfoDto2 = mock(OrderInfoDto.class, RETURNS_DEEP_STUBS);
        given(orderRepositoryMock.getActiveOrdersByTableId(A_TABLE_ID)).willReturn(List.of(orderInfoDto));
        given(orderRepositoryMock.getActiveOrdersByTableId(ANOTHER_TABLE_ID)).willReturn(List.of(orderInfoDto2));
        given(orderInfoDto.getDetails().getProductPriceFromOrder(A_PRODUCT_ID)).willReturn(BigDecimal.ONE);
        given(orderInfoDto.getDetails().getProductPriceFromOrder(ANOTHER_PRODUCT_ID)).willReturn(BigDecimal.ZERO);
        given(orderInfoDto2.getDetails().getProductPriceFromOrder(A_PRODUCT_ID)).willReturn(BigDecimal.ZERO);
        given(orderInfoDto2.getDetails().getProductPriceFromOrder(ANOTHER_PRODUCT_ID)).willReturn(BigDecimal.ONE);

        orderService.payForProducts(List.of(A_TABLE_ID, ANOTHER_TABLE_ID), List.of(A_PRODUCT_ID, ANOTHER_PRODUCT_ID));

        then(orderInfoDto.getDetails()).should().markOrderProductAsPaid(A_PRODUCT_ID);
        then(orderInfoDto2.getDetails()).should().markOrderProductAsPaid(A_PRODUCT_ID);
        then(orderInfoDto.getDetails()).should().markOrderProductAsPaid(A_PRODUCT_ID);
        then(orderInfoDto2.getDetails()).should().markOrderProductAsPaid(ANOTHER_PRODUCT_ID);
    }
}