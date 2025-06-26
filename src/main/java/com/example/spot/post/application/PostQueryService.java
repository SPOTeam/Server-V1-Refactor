package com.example.spot.post.application;

import com.example.spot.comment.presentation.dto.CommentResponse;
import com.example.spot.post.presentation.dto.response.PostAnnouncementResponse;
import com.example.spot.post.presentation.dto.response.PostBest5Response;
import com.example.spot.post.presentation.dto.response.PostPagingResponse;
import com.example.spot.post.presentation.dto.response.PostRepresentativeResponse;
import com.example.spot.post.presentation.dto.response.PostSingleResponse;
import org.springframework.data.domain.Pageable;

@Deprecated
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
