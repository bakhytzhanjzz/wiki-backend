package com.kz.wiki.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ApplicationProperties {
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Jwt {
        private String secret = "your-256-bit-secret-key-must-be-at-least-32-characters-long-for-hs256";
        private Long expiration = 86400000L; // 24 hours
        private Long refreshExpiration = 604800000L; // 7 days
    }
}







