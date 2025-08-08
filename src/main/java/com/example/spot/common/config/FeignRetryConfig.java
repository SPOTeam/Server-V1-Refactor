package com.example.spot.common.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfig {

    @Bean
    Retryer retryer() {
        return new Retryer.Default(200, 800, 3);
    }
}