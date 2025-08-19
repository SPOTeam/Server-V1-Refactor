package com.example.spot.auth.application.refactor.impl;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.infrastructure.jpa.RefreshTokenRepository;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import com.example.spot.member.infrastructure.jpa.PreferredRegionRepository;
import com.example.spot.member.infrastructure.jpa.PreferredThemeRepository;
import com.example.spot.member.infrastructure.jpa.StudyJoinReasonRepository;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuthMemberProcessor {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final PreferredThemeRepository preferredThemeRepository;
    private final PreferredRegionRepository preferredRegionRepository;
    private final StudyJoinReasonRepository studyJoinReasonRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public MemberResponseDTO.SocialLoginSignInDTO processOAuthMember(OAuthProfile oAuthProfile) {
        // 다른 로그인 타입으로 가입된 경우
        if (memberRepository.existsByEmailAndLoginTypeNot(oAuthProfile.email(), oAuthProfile.loginType())) {
            Member existing = memberRepository.findByEmail(oAuthProfile.email())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
            if (existing.getInactive() != null) {
                refreshTokenRepository.deleteByMemberId(existing.getId());
                memberRepository.deleteById(existing.getId());
                entityManager.flush();
            } else {
                throw new GeneralException(ErrorStatus._MEMBER_EMAIL_EXIST);
            }
        }

        boolean isSpotMember = false;
        Member member = memberRepository.findByEmail(oAuthProfile.email()).orElse(null);

        if (member != null && member.getInactive() != null) {
            refreshTokenRepository.deleteByMemberId(member.getId());
            memberRepository.deleteById(member.getId());
            entityManager.flush();
            member = null;
        }

        if (member == null) {
            Member memberByOAuth = Member.toMemberByOAuth(oAuthProfile.loginType(), oAuthProfile.nickname(),
                    oAuthProfile.email(),
                    oAuthProfile.profileImageUrl());
            member = memberRepository.save(memberByOAuth);
        }

        isSpotMember = checkIsSpotMember(member);

        TokenResponseDTO.TokenDTO token = tokenProvider.createToken(member.getId());
        saveRefreshToken(member, token);

        return MemberResponseDTO.SocialLoginSignInDTO.toDTO(isSpotMember,
                MemberResponseDTO.MemberSignInDTO.builder()
                        .tokens(token)
                        .memberId(member.getId())
                        .loginType(member.getLoginType())
                        .email(member.getEmail())
                        .build());
    }

    private void saveRefreshToken(Member member, TokenResponseDTO.TokenDTO token) {
        refreshTokenRepository.deleteAllByMemberId(member.getId());
        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(member.getId())
                .token(token.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    private boolean checkIsSpotMember(Member member) {
        Long id = member.getId();
        return preferredThemeRepository.existsByMemberId(id) &&
                preferredRegionRepository.existsByMemberId(id) &&
                studyJoinReasonRepository.existsByMemberId(id);
    }
}