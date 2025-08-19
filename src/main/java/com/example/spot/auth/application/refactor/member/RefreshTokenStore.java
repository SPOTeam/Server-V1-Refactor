package com.example.spot.auth.application.refactor.member;

import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.infrastructure.jpa.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final RefreshTokenRepository refreshTokenRepository;

    public void replace(Long memberId, String refresh) {
        refreshTokenRepository.deleteAllByMemberId(memberId);
        refreshTokenRepository.save(RefreshToken.builder()
                .memberId(memberId)
                .token(refresh)
                .build());
    }
}
