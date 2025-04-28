package com.example.spot.legacy.service.post;

import com.example.spot.legacy.web.dto.post.CommentResponse;
import com.example.spot.legacy.web.dto.post.PostAnnouncementResponse;
import com.example.spot.legacy.web.dto.post.PostBest5Response;
import com.example.spot.legacy.web.dto.post.PostPagingResponse;
import com.example.spot.legacy.web.dto.post.PostRepresentativeResponse;
import com.example.spot.legacy.web.dto.post.PostSingleResponse;
import com.example.spot.web.dto.post.*;
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
