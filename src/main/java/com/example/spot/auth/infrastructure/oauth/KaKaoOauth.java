package com.example.spot.auth.infrastructure.oauth;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.CLIENT_ID;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.CONTENT_TYPE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.GRANT_TYPE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.KEY_VALUE_DELIMITER;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.QUERY_DELIMITER;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.QUERY_PREFIX;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.REDIRECT_URI;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.RESPONSE_TYPE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.RESPONSE_TYPE_CODE;

import com.example.spot.auth.infrastructure.client.kakao.KaKaoApiClient;
import com.example.spot.auth.infrastructure.client.kakao.KaKaoAuthClient;
import com.example.spot.auth.infrastructure.constants.JwtConstants;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.kakao.KaKaoUser;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KaKaoOauth {

    @Value("${spring.oauth2.kakao.url}")
    private String KAKAO_SNS_URL;

    @Value("${spring.oauth2.kakao.client-id}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${spring.oauth2.kakao.callback-login-url}")
    private String KAKAO_SNS_CALLBACK_LOGIN_URL;

    private final KaKaoApiClient kaKaoApiClient;
    private final KaKaoAuthClient kaKaoAuthClient;

    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put(CLIENT_ID, KAKAO_SNS_CLIENT_ID);
        params.put(REDIRECT_URI, KAKAO_SNS_CALLBACK_LOGIN_URL);
        params.put(RESPONSE_TYPE, RESPONSE_TYPE_CODE);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + KEY_VALUE_DELIMITER + x.getValue())
                .collect(Collectors.joining(QUERY_DELIMITER));

        return KAKAO_SNS_URL + QUERY_PREFIX + parameterString;
    }

    public KaKaoOAuthTokenDTO requestAccessToken(String code) {
        return kaKaoAuthClient.getKaKaoAccessToken(
                CONTENT_TYPE, GRANT_TYPE, KAKAO_SNS_CALLBACK_LOGIN_URL, KAKAO_SNS_CLIENT_ID, code);
    }


    public KaKaoUser requestUserInfo(KaKaoOAuthTokenDTO kaKaoOAuthTokenDTO) {
        return kaKaoApiClient.getKaKaoUserInfo(
                getAccessToken(kaKaoOAuthTokenDTO), CONTENT_TYPE);
    }

    private static String getAccessToken(KaKaoOAuthTokenDTO kaKaoOAuthTokenDTO) {
        return JwtConstants.BEARER_PREFIX + kaKaoOAuthTokenDTO.access_token();
    }


}
