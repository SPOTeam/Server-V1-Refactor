package com.example.spot.post.presentation.controller.query;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.post.application.query.GetPostUseCase;
import com.example.spot.post.presentation.dto.response.comment.CommentResponse;
import com.example.spot.post.presentation.dto.response.post.PostAnnouncementResponse;
import com.example.spot.post.presentation.dto.response.post.PostBest5Response;
import com.example.spot.post.presentation.dto.response.post.PostPagingResponse;
import com.example.spot.post.presentation.dto.response.post.PostRepresentativeResponse;
import com.example.spot.post.presentation.dto.response.post.PostSingleResponse;
import com.example.spot.post.presentation.validator.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/spot/posts")
@Tag(name = "게시판 조회", description = "게시글 조회/목록/베스트/대표/공지/스크랩")
public class GetPostController {

    private final GetPostUseCase getPostUseCase;

    @Operation(
            summary = "[게시판] 게시글 단건 조회",
            description = """
                    게시글 ID로 상세 정보를 조회합니다.
                    - likeOrScrap=true면 '좋아요/스크랩 여부' 표시(로그인 필요)
                    """
    )
    @SecurityRequirement(name = "accessToken")
    @GetMapping("/{postId}")
    public ApiResponse<PostSingleResponse> getSingle(
            @Parameter(description = "게시글 ID", schema = @Schema(type = "integer", format = "int64"))
            @PathVariable @ExistPost Long postId,
            @RequestParam(required = false, defaultValue = "false") boolean likeOrScrap
    ) {
        PostSingleResponse response = getPostUseCase.getPostById(postId, likeOrScrap);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "[게시판] 게시글 페이지 조회",
            description = """
                    게시글 종류(type)별 페이지네이션 조회.
                    - type: ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT
                    - 기본 정렬은 createdAt desc(서비스 규칙에 맞춰 구현)
                    """
    )
    @GetMapping
    public ApiResponse<PostPagingResponse> getPaging(
            @Parameter(description = "게시글 종류", example = "JOB_TALK")
            @RequestParam String type,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        PostPagingResponse response = getPostUseCase.getPagingPosts(type, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "[게시판] Best 인기글 조회",
            description = "인기글 5개를 조회합니다. sortType: REAL_TIME, RECOMMEND, COMMENT"
    )
    @GetMapping("/best")
    public ApiResponse<PostBest5Response> getBest(
            @RequestParam(required = false, defaultValue = "REAL_TIME") String sortType
    ) {
        PostBest5Response response = getPostUseCase.getPostBest(sortType);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(
            summary = "[게시판] 홈 대표 게시글 조회",
            description = "게시판 홈에 노출할 종류별 대표 1건씩 조회"
    )
    @GetMapping("/representative")
    public ApiResponse<PostRepresentativeResponse> getRepresentative() {
        PostRepresentativeResponse response = getPostUseCase.getRepresentativePosts();
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "[게시판] 공지 조회", description = "공지 목록/단건(서비스 정책에 맞춰 구현)")
    @GetMapping("/announcement")
    public ApiResponse<PostAnnouncementResponse> getAnnouncement() {
        PostAnnouncementResponse response = getPostUseCase.getPostAnnouncements();
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시글 스크랩", description = "스크랩 조회 API")
    @Operation(
            summary = "[마이페이지] 스크랩한 게시글 페이지 조회",
            description = """
                    로그인 사용자의 스크랩 목록을 종류별로 페이지네이션 조회.
                    - type: ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT
                    """
    )
    @SecurityRequirement(name = "accessToken")
    @GetMapping("/scraps")
    public ApiResponse<PostPagingResponse> getScraps(
            @RequestParam String type,
            @ParameterObject @PageableDefault Pageable pageable
    ) {
        PostPagingResponse response = getPostUseCase.getScrapPagingPost(type, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판 - 댓글", description = "댓글 조회 API")
    @Operation(
            summary = "!테스트용! 게시글 댓글 조회",
            description = "단건 조회에 포함되어 있으나 테스트 목적으로 분리 제공"
    )
    @GetMapping("/{postId}/comments")
    public ApiResponse<CommentResponse> getComments(
            @Parameter(description = "게시글 ID", schema = @Schema(type = "integer", format = "int64"))
            @PathVariable @ExistPost Long postId
    ) {
        CommentResponse response = getPostUseCase.getCommentsByPostId(postId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}