package com.example.spot.report.infrastructure.jpa;

import com.example.spot.post.domain.enums.PostStatus;
import com.example.spot.report.domain.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    boolean existsByPostIdAndPostStatus(Long postId, PostStatus postStatus);
}
