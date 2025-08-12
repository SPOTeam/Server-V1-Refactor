package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.PostStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatsRepository extends JpaRepository<PostStats, Long> {

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


    @Query("select s.scrapNum from PostStats s where s.id = :postId")
    long getScrapNum(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.scrapNum = s.scrapNum + 1 where s.id = :postId")
    void incrementScrap(@Param("postId") Long postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.scrapNum = s.scrapNum - 1 where s.id = :postId and s.scrapNum > 0")
    int decrementScrap(@Param("postId") Long postId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PostStats s set s.hitCount = s.hitCount + 1 where s.id = :postId")
    void incrementHit(@Param("postId") Long postId);
}
