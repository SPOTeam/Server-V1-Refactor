package com.example.spot.auth.presentation.dto;

// 구글(서드파티)로 액세스 토큰을 보내 받아올 구글에 등록된 사용자 정보
public record GoogleUser(
        String id,
        String email,
        Boolean verifiedEmail,
        String name,
        String givenName,
        String familyName,
        String picture,
        String locale
) {
    // 필요한 경우, 여기에 메서드 정의 가능 (예: toEntity)
}
