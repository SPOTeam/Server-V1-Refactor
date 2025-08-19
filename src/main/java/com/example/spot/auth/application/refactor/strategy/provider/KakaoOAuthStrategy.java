package com.example.spot.auth.application.refactor.strategy.provider;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.auth.application.refactor.strategy.OAuthStrategy;
import com.example.spot.auth.infrastructure.oauth.KaKaoOauth;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoUser;
import com.example.spot.member.domain.enums.LoginType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthStrategy implements OAuthStrategy {

    private final KaKaoOauth kaKaoOauth;

    @Override
    public LoginType getType() {
        return LoginType.KAKAO;
    }

    @Override
    public String getOauthRedirectURL() {
        return kaKaoOauth.getOauthRedirectURL();
    }

    @Override
    public OAuthProfile getOAuthProfile(String code) {
        KaKaoOAuthTokenDTO token = kaKaoOauth.requestAccessToken(code);
        KaKaoUser user = kaKaoOauth.requestUserInfo(token);
        return OAuthProfile.of(getType(), user.kakao_account().email(), user.properties().nickname(),
                user.properties().profile_image());
    }
}
