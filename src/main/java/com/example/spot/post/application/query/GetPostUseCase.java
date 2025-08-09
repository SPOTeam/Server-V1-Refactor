package com.example.spot.post.application.query;

import com.example.spot.post.presentation.dto.response.comment.CommentResponse;
import com.example.spot.post.presentation.dto.response.post.PostAnnouncementResponse;
import com.example.spot.post.presentation.dto.response.post.PostBest5Response;
import com.example.spot.post.presentation.dto.response.post.PostPagingResponse;
import com.example.spot.post.presentation.dto.response.post.PostRepresentativeResponse;
import com.example.spot.post.presentation.dto.response.post.PostSingleResponse;
import org.springframework.data.domain.Pageable;

public interface GetPostUseCase {
    PostSingleResponse getPostById(Long postId, boolean likeOrScrap);

    PostPagingResponse getPagingPosts(String type, Pageable pageable);

    PostBest5Response getPostBest(String sortType);

    PostRepresentativeResponse getRepresentativePosts();

    PostAnnouncementResponse getPostAnnouncements();

    CommentResponse getCommentsByPostId(Long postId);

    //스크랩 게시글 조회
    PostPagingResponse getScrapPagingPost(String type, Pageable pageable);
}
