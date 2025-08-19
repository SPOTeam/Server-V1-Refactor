package com.example.spot.auth.application.refactor.member;

import com.example.spot.auth.application.refactor.dto.OAuthProfile;
import com.example.spot.member.domain.Member;
import com.example.spot.member.infrastructure.jpa.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthMemberCreator {

    private final MemberRepository memberRepository;

    public Member createFrom(OAuthProfile oAuthProfile) {
        Member member = Member.toMemberByOAuth(oAuthProfile.loginType(), oAuthProfile.nickname(), oAuthProfile.email(),
                oAuthProfile.profileImageUrl());
        return memberRepository.save(member);
    }
}
