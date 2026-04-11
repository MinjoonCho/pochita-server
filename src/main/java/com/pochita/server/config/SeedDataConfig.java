package com.pochita.server.config;

import com.pochita.server.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedDemoUsers(AuthService authService) {
        return args -> authService.ensureGoogleDemoUser();
    }
}
