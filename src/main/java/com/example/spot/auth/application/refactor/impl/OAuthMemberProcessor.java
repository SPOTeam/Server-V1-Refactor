package com.example.spot.auth.application.refactor.impl;

import com.example.spot.auth.domain.RefreshToken;
import com.example.spot.auth.domain.RefreshTokenRepository;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.domain.enums.LoginType;
import com.example.spot.member.infrastructure.MemberRepository;
import com.example.spot.member.infrastructure.PreferredRegionRepository;
import com.example.spot.member.infrastructure.PreferredThemeRepository;
import com.example.spot.member.infrastructure.StudyJoinReasonRepository;
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
    public MemberResponseDTO.SocialLoginSignInDTO processOAuthMember(LoginType loginType,
                                                                     Member providerMember) {
        // 다른 로그인 타입으로 가입된 경우
        if (memberRepository.existsByEmailAndLoginTypeNot(providerMember.getEmail(), loginType)) {
            Member existing = memberRepository.findByEmail(providerMember.getEmail())
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
        Member member = memberRepository.findByEmail(providerMember.getEmail()).orElse(null);

        if (member != null && member.getInactive() != null) {
            refreshTokenRepository.deleteByMemberId(member.getId());
            memberRepository.deleteById(member.getId());
            entityManager.flush();
            member = null;
        }

        if (member == null) {
            member = memberRepository.save(providerMember);
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