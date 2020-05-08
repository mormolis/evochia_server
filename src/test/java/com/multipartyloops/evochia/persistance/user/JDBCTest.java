package com.multipartyloops.evochia.persistance.user;

import com.multipartyloops.evochia.configuration.AppConfigurationProperties;
import com.multipartyloops.evochia.persistance.UuidPersistenceTransformer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class)
//@EnableConfigurationProperties(value = AppConfigurationProperties.class)
//@ActiveProfiles("test")
public abstract class JDBCTest {

    @Autowired
    private AppConfigurationProperties appConfigurationProperties;

    static DataSource dataSource = DataSourceBuilder.create()
            .driverClassName("com.mysql.cj.jdbc.Driver")
            .url("jdbc:mysql://localhost:3306/evochia_test")
            .username("evochia_developer")
            .password("apassword")
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
