package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.association.MemberScrap;
import com.example.spot.post.domain.enums.Board;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberScrapRepository extends JpaRepository<MemberScrap, Long> {
    Optional<MemberScrap> findByMemberIdAndPostId(Long memberId, Long postId);

    long countByPostId(Long postId);

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    @Query("SELECT ms FROM MemberScrap ms LEFT JOIN FETCH ms.post p WHERE ms.member.id = :memberId ORDER BY ms.createdAt DESC")
    Page<MemberScrap> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT ms FROM MemberScrap ms LEFT JOIN FETCH ms.post p WHERE ms.member.id = :memberId AND p.board = :board ORDER BY ms.createdAt DESC")
    Page<MemberScrap> findByMemberIdAndPost_Board(@Param("memberId") Long memberId, @Param("board") Board board,
                                                  Pageable pageable);

    @Query("DELETE from MemberScrap ms where ms.member.id = :memberId and ms.post.id = :postId")
    int deleteByPostIdAndMemberId(Long postId, Long memberId);
}
