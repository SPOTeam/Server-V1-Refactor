package com.example.spot.auth.application.refactor.impl;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.auth.application.refactor.dto.SocialAccountResult;
import com.example.spot.auth.application.refactor.member.OAuthMemberConflictProcessor;
import com.example.spot.auth.application.refactor.member.OAuthMemberCreator;
import com.example.spot.auth.application.refactor.member.ProfileCompletenessChecker;
import com.example.spot.auth.application.refactor.member.RefreshTokenStore;
import com.example.spot.auth.presentation.dto.token.TokenResponseDTO.TokenDTO;
import com.example.spot.common.security.utils.JwtTokenProvider;
import com.example.spot.member.domain.Member;
import com.example.spot.member.presentation.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OAuthMemberProcessor {

    private final OAuthMemberCreator oAuthMemberCreator;
    private final OAuthMemberConflictProcessor oAuthMemberConflictProcessor;
    private final ProfileCompletenessChecker profileCompletenessChecker;
    private final JwtTokenProvider tokenService; // TODO 인터페이스 분리
    private final RefreshTokenStore refreshTokenStore;

    @Transactional
    public MemberResponseDTO.SocialLoginSignInDTO processOAuthMember(OAuthProfile oAuthProfile) {
        SocialAccountResult socialAccountResult = oAuthMemberConflictProcessor.resolveConflict(oAuthProfile);
        Member member = socialAccountResult.member().orElseGet(() -> oAuthMemberCreator.createFrom(oAuthProfile));

        boolean isSpotMember = profileCompletenessChecker.isComplete(member.getId());

        TokenDTO token = tokenService.createToken(member.getId());
        refreshTokenStore.replace(member.getId(), token.getRefreshToken());

        return MemberResponseDTO.SocialLoginSignInDTO.toDTO(
                isSpotMember,
                MemberResponseDTO.MemberSignInDTO.builder()
                        .tokens(token)
                        .memberId(member.getId())
                        .loginType(member.getLoginType())
                        .email(member.getEmail())
                        .build());
    }
}