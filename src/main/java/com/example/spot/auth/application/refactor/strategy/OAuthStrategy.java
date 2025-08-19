package com.example.spot.auth.application.refactor.strategy;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.member.domain.enums.LoginType;

public interface OAuthStrategy {

    LoginType getType();

    String getOauthRedirectURL();

    OAuthProfile getOAuthProfile(String code); // 전략별 구현에서 Member 객체 생성
}
