package com.multipartyloops.evochia.persistance;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

public abstract class JDBCTest {

    static protected DataSource testDbDataSource = DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost:3306/evochia_test")
            .username("evochia_developer")
            .password("apassword")
            .build();

    @BeforeAll
    static protected void setupCleanDatabase() {
        Flyway flyway = Flyway.configure().dataSource(testDbDataSource).load();
        flyway.clean();
        flyway.migrate();
    }
}
