package com.kz.wiki.config;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("prod")
public class DatabaseConfig implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        String databaseUrl = environment.getProperty("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.startsWith("jdbc:")) {
            try {
                // Parse Railway/Heroku style DATABASE_URL: postgresql://user:pass@host:port/dbname
                String decodedUrl = URLDecoder.decode(databaseUrl, StandardCharsets.UTF_8);
                URI dbUri = new URI(decodedUrl.replace("postgresql://", "http://"));
                
                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] credentials = userInfo.split(":", 2);
                    String username = credentials[0];
                    String password = credentials.length > 1 ? credentials[1] : "";
                    
                    String host = dbUri.getHost();
                    int port = dbUri.getPort() > 0 ? dbUri.getPort() : 5432;
                    String dbName = dbUri.getPath().replaceFirst("/", "");
                    
                    String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
                    
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("spring.datasource.url", jdbcUrl);
                    properties.put("spring.datasource.username", username);
                    properties.put("spring.datasource.password", password);
                    
                    environment.getPropertySources().addFirst(
                            new MapPropertySource("databaseConfig", properties)
                    );
                }
            } catch (Exception e) {
                System.err.println("Failed to parse DATABASE_URL: " + e.getMessage());
            }
        }
    }
}

