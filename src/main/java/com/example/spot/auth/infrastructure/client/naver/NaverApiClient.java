package com.example.spot.auth.infrastructure.client.naver;

import com.example.spot.auth.presentation.dto.oauth.naver.NaverUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naverApiClient", url = "https://openapi.naver.com")
public interface NaverApiClient {

    @GetMapping("/v1/nid/me")
    NaverUser getNaverUserInfo(
            @RequestHeader("Authorization") String accessToken);
}
