package com.example.spot.auth.infrastructure.client.naver;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.CLIENT_ID;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.CLIENT_SECRET;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.GRANT_TYPE;

import com.example.spot.auth.presentation.dto.oauth.naver.NaverOAuthTokenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverAuthClient", url = "https://nid.naver.com/oauth2.0")
public interface NaverAuthClient {

    @GetMapping("/token")
    NaverOAuthTokenDTO getNaverAccessToken(
            @RequestParam(GRANT_TYPE) String grantType,
            @RequestParam(CLIENT_ID) String clientId,
            @RequestParam(CLIENT_SECRET) String clientSecret,
            @RequestParam String code,
            @RequestParam String state
    );


}
