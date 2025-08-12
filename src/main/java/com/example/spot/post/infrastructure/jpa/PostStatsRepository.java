package com.example.spot.post.infrastructure.jpa;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.PostHandler;
import com.example.spot.post.domain.PostStats;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

    // 파생 쿼리: 연관의 id를 경로로
    Optional<PostStats> findByPost_Id(Long postId);

    default PostStats getByPostId(Long postId) {
        return findByPost_Id(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus._POST_NOT_FOUND));
    }

    @Query("select s.likeCount from PostStats s where s.id = :postId")
    long getLikeCount(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.likeCount = s.likeCount + 1 where s.id = :postId")
    void incrementLike(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.likeCount = s.likeCount - 1 where s.id = :postId and s.likeCount > 0")
    int decrementLike(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.commentCount = s.commentCount + 1 where s.id = :postId")
    void incrementComment(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.hitCount = s.hitCount + 1 where s.id = :postId")
    void incrementHit(@Param("postId") Long postId);
}
