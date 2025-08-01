package com.example.spot.auth.presentation.dto;

// 구글에 일회성 코드를 다시 보내 받아올 액세스 토큰을 포함한 JSON 문자열을 담을 클래스
public record GoogleOAuthToken(
        String access_token,
        int expires_in,
        String scope,
        String token_type,
        String id_token
) {
}
