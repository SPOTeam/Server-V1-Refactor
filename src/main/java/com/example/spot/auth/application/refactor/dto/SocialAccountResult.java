package com.example.spot.auth.application.refactor.dto;

import com.example.spot.member.domain.Member;
import java.util.Optional;

public record SocialAccountResult(
        Optional<Member> member
) {
    public static SocialAccountResult empty() {
        return new SocialAccountResult(Optional.empty());
    }

    public static SocialAccountResult of(Member member) {
        return new SocialAccountResult(Optional.of(member));
    }
}
