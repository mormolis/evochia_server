package com.multipartyloops.evochia.persistance.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipartyloops.evochia.core.order.aggregates.OrderDetails;
import com.multipartyloops.evochia.core.order.dtos.OrderInfoDto;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import com.multipartyloops.evochia.persistance.table.TableJDBCTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

abstract public class OrderJDBCTest extends TableJDBCTest {

    protected JdbcTemplate jdbcTemplate = new JdbcTemplate(testDbDataSource);
    protected UuidPersistenceTransformer uuidPersistenceTransformer = new UuidPersistenceTransformer();
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected OrderInfoDto insertAnOrderFor(String tableId, String userId, boolean active, boolean canceled, LocalDateTime updated, String comments, OrderDetails details) {
        OrderInfoDto orderInfoDto = new OrderInfoDto(UUID.randomUUID().toString(), tableId, userId, active, canceled, updated, comments, details);
        jdbcTemplate.update("INSERT INTO order_info (order_id, table_id, user_id, active, canceled, last_updated, comments, details) VALUES  (?, ?, ?, ?, ?, ?, ?, ?)",
                            uuidPersistenceTransformer.fromString(orderInfoDto.getOrderId()),
                            uuidPersistenceTransformer.fromString(tableId),
                            uuidPersistenceTransformer.fromString(userId),
                            active,
                            canceled,
                            orderInfoDto.getLastUpdated(),
                            comments,
                            details.toJsonString(OBJECT_MAPPER));
        return orderInfoDto;
    }

    protected void insertNewTable(String tableId, String tableGroupId) {
        jdbcTemplate.update("INSERT INTO table_info (table_id, table_alias, group_id, enabled) VALUES (?, ?, ?, ?)",
                            uuidPersistenceTransformer.fromString(tableId),
                            "a-table-alias-" + UUID.randomUUID(),
                            uuidPersistenceTransformer.fromString(tableGroupId),
                            true
        );
    }

    protected String insertNewTableGroup() {
        String aTableGroupId = UUID.randomUUID().toString();
        jdbcTemplate.update("INSERT INTO table_grouping (group_id, group_name, enabled) VALUES (?, ?, ?)",
                            uuidPersistenceTransformer.fromString(aTableGroupId),
                            "aGroupName-" + UUID.randomUUID(),
                            true
        );
        return aTableGroupId;
    }

    protected void insertNewUser(String userId) {
        jdbcTemplate.update("INSERT INTO users (user_id, username, password, name, telephone) VALUES (?, ?, ?, ?, ?)",
                            uuidPersistenceTransformer.fromString(userId),
                            "aUsername-" + UUID.randomUUID(),
                            "aPassword",
                            "aName",
                            "aTelephone");
    }

}
