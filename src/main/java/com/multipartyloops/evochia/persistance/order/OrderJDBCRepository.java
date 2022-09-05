package com.multipartyloops.evochia.persistance.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.exceptions.UpdateFailedException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.CANCEL_ORDER;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.DELETE_ORDER;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.GET_ALL_ACTIVE_ORDERS;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.GET_ALL_ACTIVE_ORDERS_BY_TABLE_ID;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.GET_ALL_ORDERS;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.GET_ORDER_BY_ID;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.INSERT_NEW_ORDER;
import static com.multipartyloops.evochia.persistance.order.OrderSQLStatements.UPDATE_ORDER;

@Repository
public class OrderJDBCRepository implements OrderRepository<OrderInfoDto> {

    private final JdbcTemplate jdbcTemplate;
    private final UuidPersistenceTransformer uuidPersistenceTransformer;
    private final ObjectMapper objectMapper;

    public OrderJDBCRepository(JdbcTemplate jdbcTemplate, UuidPersistenceTransformer uuidPersistenceTransformer, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.uuidPersistenceTransformer = uuidPersistenceTransformer;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderInfoDto addNewOrder(String tableId, String userId, String comments, OrderDetails details) {
        OrderInfoDto orderInfoDto = new OrderInfoDto(
                UUID.randomUUID().toString(),
                tableId,
                userId,
                true,
                false,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                comments,
                details);

        jdbcTemplate.update(INSERT_NEW_ORDER,
                            uuidPersistenceTransformer.fromString(orderInfoDto.getOrderId()),
                            uuidPersistenceTransformer.fromString(orderInfoDto.getTableId()),
                            uuidPersistenceTransformer.fromString(orderInfoDto.getUserId()),
                            orderInfoDto.getActive(),
                            orderInfoDto.getCanceled(),
                            orderInfoDto.getLastUpdated(),
                            orderInfoDto.getComments(),
                            orderInfoDto.getDetails().toJsonString(objectMapper));
        return orderInfoDto;
    }

    @Override
    public Optional<OrderInfoDto> getOrderById(String orderId) {
        Object orderIdInbytes = uuidPersistenceTransformer.fromString(orderId);
        final var orders = jdbcTemplate.query(GET_ORDER_BY_ID,
                                              this::parseResultsToOrderInfo,
                                              orderIdInbytes);

        if (orders.size() == 1) {
            return Optional.of(orders.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<OrderInfoDto> getActiveOrdersByTableId(String tableId) {
        Object tableIdInBytes = uuidPersistenceTransformer.fromString(tableId);
        return jdbcTemplate.query(GET_ALL_ACTIVE_ORDERS_BY_TABLE_ID,
                                  this::parseResultsToOrderInfo,
                                  tableIdInBytes);
    }

    @Override
    public List<OrderInfoDto> getActiveOrders() {
        return jdbcTemplate.query(GET_ALL_ACTIVE_ORDERS,
                                  this::parseResultsToOrderInfo);
    }

    @Override
    public List<OrderInfoDto> getAllOrders() {
        return jdbcTemplate.query(GET_ALL_ORDERS,
                                  this::parseResultsToOrderInfo);
    }

    @Override
    public void updateOrder(OrderInfoDto orderInfoDto) {
        final var affectedRows = jdbcTemplate.update(UPDATE_ORDER,
                                                     uuidPersistenceTransformer.fromString(orderInfoDto.getTableId()),
                                                     uuidPersistenceTransformer.fromString(orderInfoDto.getUserId()),
                                                     orderInfoDto.getComments(),
                                                     orderInfoDto.getCanceled(),
                                                     orderInfoDto.getActive(),
                                                     LocalDateTime.now(),
                                                     orderInfoDto.getDetails().toJsonString(objectMapper),
                                                     uuidPersistenceTransformer.fromString(orderInfoDto.getOrderId())
        );

        throwIfUpdateFailed(affectedRows);
    }

    @Override
    public void cancelOrder(String orderId, String comments) {
        final var affectedRows = jdbcTemplate.update(CANCEL_ORDER,
                                                     comments, uuidPersistenceTransformer.fromString(orderId));
        throwIfUpdateFailed(affectedRows);
    }

    @Override
    public void deleteOrder(String orderId) {
        final var affectedRows = jdbcTemplate.update(DELETE_ORDER,
                                                     uuidPersistenceTransformer.fromString(orderId));
        throwIfUpdateFailed(affectedRows);
    }

    private OrderInfoDto parseResultsToOrderInfo(ResultSet resultSet, int _i) throws SQLException {
        return new OrderInfoDto(
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("order_id")),
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("table_id")),
                uuidPersistenceTransformer.getUUIDFromBytes(resultSet.getBytes("user_id")),
                resultSet.getBoolean("active"),
                resultSet.getBoolean("canceled"),
                resultSet.getTimestamp("last_updated").toLocalDateTime(),
                resultSet.getString("comments"),
                convertToObject(resultSet.getString("details"))
        );
    }

    private OrderDetails convertToObject(String details) {
        try {
            return objectMapper.readValue(details, OrderDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwIfUpdateFailed(int affectedRows) {
        if (affectedRows == 0) {
            throw new UpdateFailedException("Order could not be updated");
        }
    }
}
