package com.example.spot.auth.application.refactor.dto;

import com.example.spot.member.domain.enums.LoginType;

public record OAuthProfile(
        LoginType loginType,
        String email,
        String nickname,
        String profileImageUrl
) {

    public static OAuthProfile of(LoginType loginType, String email, String nickname, String profileImageUrl) {
        return new OAuthProfile(loginType, email, nickname, profileImageUrl);
    }
}
