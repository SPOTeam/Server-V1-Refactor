package com.example.spot.report.domain;

import com.example.spot.post.domain.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);

    boolean existsByPostIdAndPostStatus(Long postId, PostStatus postStatus);
}
