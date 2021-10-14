package com.multipartyloops.evochia.persistance.order;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderJDBCRepositoryTest extends OrderJDBCTest {

    private OrderJDBCRepository orderJDBCRepository = new OrderJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);

    @Test
    void retrievesOrder() {
        final var tableId = UUID.randomUUID().toString();
        final var userId = UUID.randomUUID().toString();
        final var tableGroupId = insertNewTableGroup();
        insertNewUser(userId);
        insertNewTable(tableId, tableGroupId);
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

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

        final var orderInfo = orderJDBCRepository.addNewOrder(tableId, userId, "aComment", "aDetail");

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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        final var anOrderInDifferentTable = insertAnOrderFor(anotherTableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        final var anotherOrderInfoDto = insertAnOrderFor(tableId, userId, true, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        final var anOrderInDifferentTable = insertAnOrderFor(anotherTableId, userId, true, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, true, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");
        orderInfoDto.setDetails("updated-details");
        orderInfoDto.setComments("updated-comments");
        orderInfoDto.setTableId(anotherTableId);

        orderJDBCRepository.updateOrder(orderInfoDto);

        final var retrievedOrder = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());
        assertThat(retrievedOrder).isPresent();
        assertThat(retrievedOrder.get().getDetails()).isEqualTo("updated-details");
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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

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
        final var orderInfoDto = insertAnOrderFor(tableId, userId, false, false, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "aComment", "aDetail");

        orderJDBCRepository.deleteOrder(orderInfoDto.getOrderId());

        final var retrievedOrder = orderJDBCRepository.getOrderById(orderInfoDto.getOrderId());
        assertThat(retrievedOrder).isEmpty();
    }
}
