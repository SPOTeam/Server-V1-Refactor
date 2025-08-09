package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.PostComment;
import com.example.spot.post.infrastructure.querydsl.PostCommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentRepositoryCustom {
    //List<PostComment> findByPostId(Long postId);
}
