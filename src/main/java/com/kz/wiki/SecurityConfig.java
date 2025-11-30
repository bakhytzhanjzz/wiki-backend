package com.kz.wiki;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // отключаем CSRF для тестов
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ping").permitAll()  // разрешаем всем
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {}); // <-- новый синтаксис

        return http.build();
    }
}
