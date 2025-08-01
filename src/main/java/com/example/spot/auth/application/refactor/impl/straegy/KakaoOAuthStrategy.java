package com.example.spot.auth.application.refactor.impl.straegy;

import com.example.spot.auth.application.refactor.OAuthStrategy;
import com.example.spot.auth.application.refactor.impl.oauth.KaKaoOauth;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoUser;
import com.example.spot.member.domain.Member;
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
    public Member toMember(String code) {
        KaKaoOAuthTokenDTO token = kaKaoOauth.requestAccessToken(code);
        KaKaoUser user = kaKaoOauth.requestUserInfo(token);
        return Member.toMember(getType(), user.properties().nickname(), user.properties().nickname(),
                user.properties().profile_image());
    }
}
