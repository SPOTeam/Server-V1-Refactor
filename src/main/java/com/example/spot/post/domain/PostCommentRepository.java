package com.example.spot.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>, PostCommentRepositoryCustom {
    //List<PostComment> findByPostId(Long postId);
}
