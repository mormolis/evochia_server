package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public abstract class JDBCTest {

    static DataSource dataSource = DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost:3306/evochia_test")
            .username("root")
            .build();

    @BeforeAll
    static void setupCleanDatabase() {
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
    }


    UserJDBCRepository userJDBCRepository;

    @BeforeEach
    void setup() {
        userJDBCRepository = new UserJDBCRepository(new JdbcTemplate(dataSource), new UuidPersistenceTransformer());
    }
}
