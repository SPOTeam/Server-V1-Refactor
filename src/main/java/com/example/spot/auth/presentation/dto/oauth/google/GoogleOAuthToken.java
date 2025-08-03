package com.example.spot.auth.presentation.dto.oauth.google;

public record GoogleOAuthToken(
        String access_token,
        int expires_in,
        String scope,
        String token_type,
        String id_token
) {
}
