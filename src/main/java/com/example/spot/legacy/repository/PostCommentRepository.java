package com.example.spot.legacy.repository;

import com.example.spot.refactor.comment.domain.PostComment;
import com.example.spot.legacy.repository.querydsl.PostCommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentRepositoryCustom {
    //List<PostComment> findByPostId(Long postId);
}
