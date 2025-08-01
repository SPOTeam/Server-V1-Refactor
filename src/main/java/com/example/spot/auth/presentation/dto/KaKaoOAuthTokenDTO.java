package com.example.spot.auth.presentation.dto;

public record KaKaoOAuthTokenDTO(
        String token_type,
        String access_token,
        String refresh_token,
        String expires_in,
        String refresh_token_expires_in
) {
}
