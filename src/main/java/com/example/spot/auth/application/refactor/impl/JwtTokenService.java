package com.example.spot.auth.application.refactor.impl;

import com.example.spot.auth.application.refactor.TokenService;
import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.infrastructure.jpa.RefreshTokenRepository;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * 토큰을 재발급 합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰을 생성하여 반환합니다.
     * @throws GeneralException 토큰이 만료되었거나, 잘못된 토큰일 경우 발생합니다.
     */
    @Override
    public TokenResponseDTO.TokenDTO reissueToken(String refreshToken) {

        // 리프레시 토큰 조회 및 검증
        RefreshToken tokenInDB = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus._INVALID_REFRESH_TOKEN));

        // 리프레시 토큰 만료 확인
        if (jwtTokenProvider.isTokenExpired(tokenInDB.getToken())) {
            refreshTokenRepository.delete(tokenInDB);
            throw new GeneralException(ErrorStatus._EXPIRED_REFRESH_TOKEN);
        }

        // 리프레시 토큰에서 memberId 추출
        Long memberIdByToken = jwtTokenProvider.getMemberIdByToken(refreshToken);

        // memberId로 회원 조회
        Member member = memberRepository.findById(memberIdByToken)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 회원의 리프레시 토큰과 요청된 리프레시 토큰 비교
        if (!Objects.equals(member.getId(), memberIdByToken)) {
            throw new GeneralException(ErrorStatus._INVALID_JWT);
        }

        // 토큰 재발급
        TokenResponseDTO.TokenDTO tokenDTO = jwtTokenProvider.reissueToken(refreshToken);

        // 리프레시 토큰 저장
        RefreshToken token = RefreshToken.builder()
                .memberId(member.getId())
                .token(tokenDTO.getRefreshToken())
                .build();

        // 기존 리프레시 토큰 삭제
        if (refreshTokenRepository.existsByMemberId(member.getId())) {
            refreshTokenRepository.deleteByMemberId(member.getId());
        }

        // 새로운 리프레시 토큰 저장
        refreshTokenRepository.save(token);

        // 토큰 재발급
        return tokenDTO;
    }
}
