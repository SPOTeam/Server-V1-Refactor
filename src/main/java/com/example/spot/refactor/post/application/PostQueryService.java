package com.example.spot.refactor.post.application;

import com.example.spot.refactor.comment.presentation.dto.CommentResponse;
import com.example.spot.refactor.post.presentation.controller.PostAnnouncementResponse;
import com.example.spot.refactor.post.presentation.dto.PostBest5Response;
import com.example.spot.refactor.post.presentation.dto.PostPagingResponse;
import com.example.spot.refactor.post.presentation.dto.PostRepresentativeResponse;
import com.example.spot.refactor.post.presentation.dto.PostSingleResponse;
import org.springframework.data.domain.Pageable;

public interface PostQueryService {
    PostSingleResponse getPostById(Long postId, boolean likeOrScrap);

    PostPagingResponse getPagingPosts(String type, Pageable pageable);

    PostBest5Response getPostBest(String sortType);

    PostRepresentativeResponse getRepresentativePosts();

    PostAnnouncementResponse getPostAnnouncements();

    CommentResponse getCommentsByPostId(Long postId);

    //스크랩 게시글 조회
    PostPagingResponse getScrapPagingPost(String type, Pageable pageable);
}
