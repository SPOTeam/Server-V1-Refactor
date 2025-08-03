package com.example.spot.auth.infrastructure.client.kakao;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.HEADER_AUTHORIZATION;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.HEADER_CONTENT_TYPE;

import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KaKaoApiClient {

    @GetMapping("/v2/user/me")
    KaKaoUser getKaKaoUserInfo(
            @RequestHeader(HEADER_AUTHORIZATION) String accessToken,
            @RequestHeader(HEADER_CONTENT_TYPE) String contentType);
}
