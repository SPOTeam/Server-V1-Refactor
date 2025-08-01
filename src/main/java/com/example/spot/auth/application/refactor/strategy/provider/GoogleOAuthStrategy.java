package com.example.spot.auth.application.refactor.strategy.provider;

import com.example.spot.auth.application.refactor.strategy.OAuthStrategy;
import com.example.spot.auth.infrastructure.oauth.GoogleOauth;
import com.example.spot.auth.presentation.dto.oauth.google.GoogleOAuthToken;
import com.example.spot.auth.presentation.dto.oauth.google.GoogleUser;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleOAuthStrategy implements OAuthStrategy {

    private final GoogleOauth googleOauth;

    @Override
    public LoginType getType() {
        return LoginType.GOOGLE;
    }

    @Override
    public String getOauthRedirectURL() {
        return googleOauth.getOauthRedirectURL();
    }

    @Override
    public Member toMember(String code) {
        GoogleOAuthToken token = googleOauth.requestAccessToken(code);
        GoogleUser user = googleOauth.requestUserInfo(token);
        return Member.toMemberByOAuth(getType(), user.name(), user.email(), user.picture());
    }
}
