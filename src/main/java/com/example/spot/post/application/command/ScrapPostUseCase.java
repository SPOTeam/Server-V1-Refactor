package com.example.spot.post.application.command;

import com.example.spot.post.presentation.dto.request.ScrapAllDeleteRequest;
import com.example.spot.post.presentation.dto.response.ScrapPostResponse;
import com.example.spot.post.presentation.dto.response.ScrapsPostDeleteResponse;

public interface ScrapPostUseCase {

    //게시글 스크랩
    ScrapPostResponse scrapPost(Long postId, Long memberId);

    //게시글 스크랩 취소
    ScrapPostResponse cancelPostScrap(Long postId, Long memberId);

    //게시글 스크랩 모두 취소
    ScrapsPostDeleteResponse cancelPostScraps(ScrapAllDeleteRequest request);
}
