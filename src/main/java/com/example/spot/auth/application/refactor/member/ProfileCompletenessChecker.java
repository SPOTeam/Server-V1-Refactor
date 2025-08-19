package com.example.spot.auth.application.refactor.member;

import com.example.spot.member.infrastructure.jpa.PreferredRegionRepository;
import com.example.spot.member.infrastructure.jpa.PreferredThemeRepository;
import com.example.spot.member.infrastructure.jpa.StudyJoinReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileCompletenessChecker {

    private final PreferredThemeRepository themeRepository;
    private final PreferredRegionRepository regionRepository;
    private final StudyJoinReasonRepository reasonRepository;

    public boolean isComplete(Long memberId) {
        return themeRepository.existsByMemberId(memberId)
                && regionRepository.existsByMemberId(memberId)
                && reasonRepository.existsByMemberId(memberId);
    }
}
