package com.multipartyloops.evochia.persistance.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class OrderJDBCRepositoryTest extends OrderJDBCTest {


    public static final OrderDetails SOME_ORDER_DETAILS = someOrderDetails();

    private static OrderDetails someOrderDetails()  {
        try {
            return OBJECT_MAPPER.readValue("{\"orderProducts\":[{\"id\":\"a-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":2.5,\"canceled\":false},{\"id\":\"another-unique-order-product-id\",\"productId\":\"anId\",\"options\":[{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":0.5},{\"productOptionId\":\"an-id\",\"productId\":\"a-product-id\",\"variation\":\"aVariation\",\"price\":1}],\"discountPercentage\":10,\"notes\":\"anote\",\"paid\":false,\"terminalId\":\"a-terminal-id\",\"productName\":\"aproduct\",\"price\":5,\"canceled\":false}],\"totalPrice\":9.45}", OrderDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private OrderJDBCRepository orderJDBCRepository = new OrderJDBCRepository(jdbcTemplate, uuidPersistenceTransformer, OBJECT_MAPPER);

    @Test
    void retrievesOrder() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        final var orderById = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());

        assertThat(orderById).isPresent();
        assertThat(orderById.get()).isEqualTo(orderInfoDto);
    }

    @Test
    void addsNewOrder() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);

        final var orderInfo = orderJDBCRepository.addNewOrder(tableId, userId, "aComment", SOME_ORDER_DETAILS);

        final var retrieved = orderJDBCRepository.getOrderById(orderInfo.getOrderId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo(orderInfo);
    }

    @Test
    void getsAllActiveOrdersByTableId() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        final var activeOrdersByTableId = orderJDBCRepository.getActiveOrdersByTableId(tableId);

        assertThat(activeOrdersByTableId).containsExactlyInAnyOrder(orderInfoDto, anotherOrderInfoDto);
    }

    @Test
    void getsAllActiveOrders() {
        setupCleanDatabase();
        final var tableId = UUID.randomUUID().toString();
        final var anotherTableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        insertNewTable(anotherTableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        final var anOrderInDifferentTable = insertAnOrderFor(anotherTableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        final var activeOrders = orderJDBCRepository.getActiveOrders();

        assertThat(activeOrders).containsExactlyInAnyOrder(orderInfoDto, anotherOrderInfoDto, anOrderInDifferentTable);
    }

    @Test
    void getsAllOrders() {
        final var tableId = UUID.randomUUID().toString();
        final var anotherTableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        insertNewTable(anotherTableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        final var anOrderInDifferentTable = insertAnOrderFor(anotherTableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        final var allOrders = orderJDBCRepository.getAllOrders();

        assertThat(allOrders).containsExactlyInAnyOrder(orderInfoDto, anotherOrderInfoDto, anOrderInDifferentTable);
    }

    @Test
    void updatesOrders() {
        final var tableId = UUID.randomUUID().toString();
        final var anotherTableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        insertNewTable(anotherTableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);
        orderInfoDto.setDetails(SOME_ORDER_DETAILS);
        orderInfoDto.setComments("updated-comments");
        orderInfoDto.setTableId(anotherTableId);

        orderJDBCRepository.updateOrder(orderInfoDto);

        final var retrievedOrder = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getDetails()).isEqualTo(SOME_ORDER_DETAILS);
        assertThat(retrievedOrder.get().getComments()).isEqualTo("updated-comments");
        assertThat(retrievedOrder.get().getTableId()).isEqualTo(anotherTableId);
    }

    @Test
    void cancelsOrders() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        orderJDBCRepository.cancelOrder(orderInfoDto.getOrderId(), "nope");

        final var retrievedOrder = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getCanceled()).isTrue();
        assertThat(retrievedOrder.get().getComments()).isEqualTo("nope");
    }

    @Test
    void deletesOrders() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", SOME_ORDER_DETAILS);

        orderJDBCRepository.deleteOrder(orderInfoDto.getOrderId());

        final var retrievedOrder = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());
        assertThat(retrievedOrder).isEmpty();
    }
}
