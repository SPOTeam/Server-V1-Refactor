package com.example.spot.auth.application.refactor.impl.straegy;

import com.example.spot.auth.application.refactor.OAuthStrategy;
import com.example.spot.auth.application.refactor.impl.oauth.NaverOauth;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverUser;
import com.example.spot.member.domain.Member;
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
    public Member toMember(String code) {
        NaverOAuthTokenDTO token = naverOauth.requestAccessToken(code);
        NaverUser user = naverOauth.requestUserInfo(token);
        return Member.toMember(
                getType(), user.response().name(), user.response().email(),
                user.response().thumbnail_image());
    }
}
