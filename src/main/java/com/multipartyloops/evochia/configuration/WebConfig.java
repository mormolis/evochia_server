package com.multipartyloops.evochia.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public final void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "DELETE", "PATCH", "OPTIONS")

                .allowedHeaders(
                        "Host",
                        "User-Agent",
                        "X-Requested-With",
                        "Accept",
                        "Accept-Language",
                        "Accept-Encoding",
                        "Authorization",
                        "Referer",
                        "Connection",
                        "Content-Type")
                .exposedHeaders(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)
                .maxAge(3600)
                .allowCredentials(false);
    }
}