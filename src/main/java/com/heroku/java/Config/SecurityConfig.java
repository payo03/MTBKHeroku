package com.heroku.java.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/healthcheck") // API 경로는 CSRF 보호 제외
            ).authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 모든 요청 허용
            ).headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; " +
                    "script-src 'self' https://cdn.jsdelivr.net https://code.jquery.com; " +
                    "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                    "font-src 'self' https://cdn.jsdelivr.net; " +
                    "img-src 'self' http://localhost:5000; " +
                    "connect-src 'self'"
                ))
                .frameOptions(frameOptions -> frameOptions.deny())
            );

        return http.build();
    }
}
