package com.example.spot.common.config;

import com.example.spot.common.infrastructure.feign.retry.SelectiveRetryErrorDecoder;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "com.example.spot")
@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new SelectiveRetryErrorDecoder();
    }

    @Bean
    public Retryer retryer() {
        // 초기대기 200ms, 최대대기 800ms, 최대 3번 재시도
        return new Retryer.Default(200, 800, 3);
    }
}
