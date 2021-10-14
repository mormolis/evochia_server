package com.multipartyloops.evochia.persistance.order;

public final class OrderSQLStatements {

    static final String INSERT_NEW_ORDER = "INSERT INTO order_info (order_id, table_id, user_id, active, canceled, last_updated, comments, details) VALUES  (?, ?, ?, ?, ?, ?, ?, ?)";
    static final String GET_ORDER_BY_ID = "SELECT * FROM order_info WHERE order_id=?";
    static final String GET_ALL_ACTIVE_ORDERS_BY_TABLE_ID = "SELECT * FROM order_info WHERE active=true AND table_id=?";
    static final String GET_ALL_ACTIVE_ORDERS = "SELECT * from order_info WHERE active=true";
    static final String GET_ALL_ORDERS = "SELECT * FROM order_info";
    static final String UPDATE_ORDER = "UPDATE order_info SET table_id=?, user_id=?, comments=?, canceled=?, active=?, last_updated=?, details=? WHERE order_id=?";
    static final String CANCEL_ORDER = "UPDATE order_info SET canceled=true, comments=? WHERE order_id=?";
    static final String DELETE_ORDER = "DELETE FROM order_info WHERE order_id=?";

    private OrderSQLStatements() {
    }
}
