package com.example.spot.post.presentation.controller.command;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.post.application.command.ScrapPostUseCase;
import com.example.spot.post.presentation.dto.request.post.ScrapAllDeleteRequest;
import com.example.spot.post.presentation.dto.response.post.ScrapPostResponse;
import com.example.spot.post.presentation.dto.response.post.ScrapsPostDeleteResponse;
import com.example.spot.post.presentation.validator.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spot/posts")
public class ScrapPostController {

    private final ScrapPostUseCase scrapPostUseCase;

    @Tag(name = "게시글 스크랩", description = "게시글 스크랩 관련 API")
    @Operation(summary = "[게시판] 게시글 스크랩 API", description = "게시글 ID와 회원 ID를 받아 스크랩을 추가합니다.")
    @PostMapping("/{postId}/scrap")
    public ApiResponse<ScrapPostResponse> scrapPost(
            @PathVariable @ExistPost Long postId
    ) {
        ScrapPostResponse response = scrapPostUseCase.scrapPost(postId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }


    @Tag(name = "게시글 스크랩", description = "게시글 스크랩 관련 API")
    @Operation(summary = "[게시판] 게시글 스크랩 취소 API", description = "게시글 ID와 회원 ID를 받아 스크랩을 취소합니다.")
    @DeleteMapping("/{postId}/scrap")
    public ApiResponse<ScrapPostResponse> cancelPostScrap(
            @PathVariable @ExistPost Long postId
    ) {
        ScrapPostResponse response = scrapPostUseCase.cancelPostScrap(postId, SecurityUtils.getCurrentUserId());
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT, response);
    }

    @Tag(name = "게시글 스크랩", description = "게시글 스크랩 관련 API")
    @Operation(summary = "[마이페이지] 게시글 스크랩 모두 삭제 API", description = "로그인한 회원의 취소 할 스크랩 게시글 ID를 리스트 형식으로 입력받아 해당하는 모든 스크랩을 취소합니다.")
    @DeleteMapping("/scraps")
    public ApiResponse<ScrapsPostDeleteResponse> deleteAllPostScrap(
            @RequestBody ScrapAllDeleteRequest request) {
        ScrapsPostDeleteResponse response = scrapPostUseCase.cancelPostScraps(request);
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT, response);
    }
}
