package com.multipartyloops.evochia.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@ConfigurationProperties
@Configuration
public class AppConfigurationProperties {

    private Datasource datasource;

    @Data
    public static class Datasource {
        private String username;
        private String password;
        private String url;
    }
}