package com.example.spot.auth.application.refactor.member;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.auth.application.refactor.dto.SocialAccountResult;
import com.example.spot.auth.infrastructure.jpa.RefreshTokenRepository;
import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthMemberConflictProcessor {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EntityManager em;

    public SocialAccountResult resolveConflict(OAuthProfile p) {
        Optional<Member> opt = memberRepository.findByEmail(p.email());
        if (opt.isEmpty()) {
            return SocialAccountResult.empty();
        }

        Member existing = opt.get();
        // 다른 타입으로 가입된 경우
        if (existing.getLoginType() != p.loginType()) {
            if (existing.getInactive() != null) {
                refreshTokenRepository.deleteByMemberId(existing.getId());
                memberRepository.deleteById(existing.getId());
                em.flush();

                return SocialAccountResult.empty();
            }
            throw new GeneralException(ErrorStatus._MEMBER_EMAIL_EXIST);
        }

        // 같은 타입인데 탈퇴 상태면 정리
        if (existing.getInactive() != null) {
            refreshTokenRepository.deleteByMemberId(existing.getId());
            memberRepository.deleteById(existing.getId());
            em.flush();
            return SocialAccountResult.empty();
        }
        return SocialAccountResult.of(existing);
    }
}
