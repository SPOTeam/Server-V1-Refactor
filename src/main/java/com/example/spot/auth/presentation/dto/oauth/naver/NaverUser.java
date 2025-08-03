package com.example.spot.auth.presentation.dto.oauth.naver;

public record NaverUser(
        String resultcode,
        String message,
        NaverPropertiesDTO response
) {
    public record NaverPropertiesDTO(
            String id,
            String name,
            String email,
            String thumbnail_image
    ) {
    }
}