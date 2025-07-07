package com.example.spot.member.infrastructure;

import com.example.spot.member.domain.association.PreferredStudy;
import com.example.spot.study.domain.enums.StudyLikeStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

// 찜한 스터디
public interface PreferredStudyRepository extends JpaRepository<PreferredStudy, Long> {

    List<PreferredStudy> findByMemberIdAndStudyLikeStatusOrderByCreatedAtDesc(
            Long memberId, StudyLikeStatus studyLikeStatus, Pageable pageable);

    Optional<PreferredStudy> findByMemberIdAndStudyId(Long memberId, Long studyId);

    long countByMemberIdAndStudyLikeStatus(Long memberId, StudyLikeStatus studyLikeStatus);
}
