package com.example.spot.auth.application.refactor;

import com.example.spot.member.domain.enums.LoginType;

public interface OAuthStrategy {
    LoginType getType();

    String getOauthRedirectURL();

    String extractEmail(String code);
}
