package com.example.spot.auth.infrastructure.client.google;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.HEADER_AUTHORIZATION;

import com.example.spot.auth.presentation.dto.GoogleUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleApiClient", url = "https://www.googleapis.com")
public interface GoogleApiClient {

    @GetMapping("/oauth2/v2/userinfo")
    GoogleUser getGoogleUserInfo(@RequestHeader(HEADER_AUTHORIZATION) String authorization);
}
