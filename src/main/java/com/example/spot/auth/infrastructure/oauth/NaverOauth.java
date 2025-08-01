package com.example.spot.auth.infrastructure.oauth;

import static com.example.spot.auth.infrastructure.constants.AuthConstants.CLIENT_ID;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.GRANT_TYPE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.KEY_VALUE_DELIMITER;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.QUERY_DELIMITER;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.QUERY_PREFIX;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.REDIRECT_URI;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.RESPONSE_TYPE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.RESPONSE_TYPE_CODE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.STATE;
import static com.example.spot.auth.infrastructure.constants.AuthConstants.STATE_STRING;
import static com.example.spot.auth.infrastructure.constants.JwtConstants.BEARER_PREFIX;

import com.example.spot.auth.infrastructure.client.naver.NaverApiClient;
import com.example.spot.auth.infrastructure.client.naver.NaverAuthClient;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverOAuthTokenDTO;
import com.example.spot.auth.presentation.dto.oauth.naver.NaverUser;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverOauth {

    @Value("${spring.oauth2.naver.client-id}")
    private String NAVER_CLIENT_ID;

    @Value("${spring.oauth2.naver.client-secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${spring.oauth2.naver.callback-url}")
    private String NAVER_CALLBACK_LOGIN_URL;

    @Value("${spring.oauth2.naver.url}")
    private String NAVER_SNS_URL;

    private final NaverAuthClient naverAuthClient;
    private final NaverApiClient naverApiClient;

    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put(CLIENT_ID, NAVER_CLIENT_ID);
        params.put(REDIRECT_URI, NAVER_CALLBACK_LOGIN_URL);
        params.put(RESPONSE_TYPE, RESPONSE_TYPE_CODE);
        params.put(STATE, STATE_STRING);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + KEY_VALUE_DELIMITER + x.getValue())
                .collect(Collectors.joining(QUERY_DELIMITER));

        return NAVER_SNS_URL + QUERY_PREFIX + parameterString;
    }

    public NaverOAuthTokenDTO requestAccessToken(String code) {
        return naverAuthClient.getNaverAccessToken(
                GRANT_TYPE, NAVER_CLIENT_ID, NAVER_CLIENT_SECRET, code, STATE);
    }


    public NaverUser requestUserInfo(NaverOAuthTokenDTO naverOAuthTokenDTO) {
        return naverApiClient.getNaverUserInfo(
                getAccessToken(naverOAuthTokenDTO));
    }

    private static String getAccessToken(NaverOAuthTokenDTO naverOAuthTokenDTO) {
        return BEARER_PREFIX + naverOAuthTokenDTO.access_token();
    }
}
