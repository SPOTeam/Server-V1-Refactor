package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.association.LikedPost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikedPostRepository extends JpaRepository<LikedPost, Long> {
    Optional<LikedPost> findByMemberIdAndPostId(Long memberId, Long postId);

    // 게시글 ID별로 LikedPost의 개수 세기
    long countByPostId(Long postId);

    // 회원 ID와 게시글 ID로 LikedPost 존재 여부
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    // 지우고 영향 행 수 반환
    @Modifying
    @Query("delete from LikedPost lp where lp.member.id = :memberId and lp.post.id = :postId")
    int deleteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

}
