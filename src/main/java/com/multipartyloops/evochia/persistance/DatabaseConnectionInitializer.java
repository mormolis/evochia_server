//package com.multipartyloops.evochia.persistance;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//@Component
//public class DatabaseConnectionInitializer {
//
//    private final DataSource dataSource;
//
//    public DatabaseConnectionInitializer(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    public Connection getConnection() {
//        try{
//            return dataSource.getConnection();
//        } catch (SQLException e) {
//            throw new RuntimeException("Cannot get database connection", e);
//        }
//    }
//
//    public void closeConnection(Connection connection) {
//        try {
//            if(connection != null && !connection.isClosed()) {
//                connection.close();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Could not close db connection", e);
//        }
//    }
//}
