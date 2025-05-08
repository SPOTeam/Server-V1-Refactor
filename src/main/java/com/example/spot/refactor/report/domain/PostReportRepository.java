package com.example.spot.refactor.report.domain;

import com.example.spot.refactor.post.domain.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    boolean existsByPostIdAndPostStatus(Long postId, PostStatus postStatus);
}
