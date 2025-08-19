package com.example.spot.auth.application.refactor.strategy.provider;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.auth.application.refactor.strategy.OAuthStrategy;
import com.example.spot.auth.infrastructure.oauth.NaverOauth;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverUser;
import com.example.spot.member.domain.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverOAuthStrategy implements OAuthStrategy {

    private final NaverOauth naverOauth;

    @Override
    public LoginType getType() {
        return LoginType.NAVER;
    }

    @Override
    public String getOauthRedirectURL() {
        return naverOauth.getOauthRedirectURL();
    }

    @Override
    public OAuthProfile getOAuthProfile(String code) {
        NaverOAuthTokenDTO token = naverOauth.requestAccessToken(code);
        NaverUser user = naverOauth.requestUserInfo(token);
        return OAuthProfile.of(
                getType(), user.response().email(), user.response().name(),
                user.response().thumbnail_image());
    }
}
