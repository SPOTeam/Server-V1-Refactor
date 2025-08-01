package com.example.spot.auth.application.refactor.impl.straegy;

import com.example.spot.auth.application.refactor.OAuthStrategy;
import com.example.spot.auth.application.refactor.impl.oauth.GoogleOauth;
import com.example.spot.auth.presentation.dto.GoogleOAuthToken;
import com.example.spot.auth.presentation.dto.GoogleUser;
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
    public String extractEmail(String code) {
        GoogleOAuthToken token = googleOauth.requestAccessToken(code);
        GoogleUser user = googleOauth.requestUserInfo(token);
        return user.email();
    }
}
