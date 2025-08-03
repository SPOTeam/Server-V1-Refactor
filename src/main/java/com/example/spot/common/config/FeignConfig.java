package com.example.spot.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.example.spot")
@Configuration
public class FeignConfig {
}
