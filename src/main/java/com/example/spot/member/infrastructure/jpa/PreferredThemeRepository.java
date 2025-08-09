package com.example.spot.member.infrastructure.jpa;

import com.example.spot.member.domain.association.PreferredTheme;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 관심 분야
@Repository
public interface PreferredThemeRepository extends JpaRepository<PreferredTheme, Long> {
    List<PreferredTheme> findAllByMemberId(Long memberId);

    PreferredTheme findByMemberId(Long memberId);

    void deleteByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

}
