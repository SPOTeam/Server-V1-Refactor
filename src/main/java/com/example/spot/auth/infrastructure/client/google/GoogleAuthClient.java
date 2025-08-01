package com.example.spot.auth.infrastructure.client.google;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.HEADER_CONTENT_TYPE;

import com.example.spot.auth.presentation.dto.oauth.google.GoogleOAuthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "googleAuthClient", url = "https://oauth2.googleapis.com")
public interface GoogleAuthClient {

    @PostMapping("/token")
    GoogleOAuthToken getGoogleAccessToken(
            @RequestHeader(HEADER_CONTENT_TYPE) String contentType,
            @RequestParam String code,
            @RequestParam String clientId,
            @RequestParam String clientSecret,
            @RequestParam String redirectUri,
            @RequestParam String grantType
    );
}
