package com.example.spot.post.presentation.controller.query;

import com.example.spot.comment.presentation.dto.CommentResponse;
import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.post.application.query.GetPostUseCase;
import com.example.spot.post.presentation.dto.response.PostAnnouncementResponse;
import com.example.spot.post.presentation.dto.response.PostBest5Response;
import com.example.spot.post.presentation.dto.response.PostPagingResponse;
import com.example.spot.post.presentation.dto.response.PostRepresentativeResponse;
import com.example.spot.post.presentation.dto.response.PostSingleResponse;
import com.example.spot.post.presentation.validator.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class GetPostController {

    private final GetPostUseCase getPostUseCase;

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시글 단건 조회 API",
            description = """
        게시글 ID를 받아 게시글을 조회합니다. 
        
        해당 게시글에 대한 상세 정보를 반환합니다. 
        
        좋아요나 스크랩으로 인한 조회 시 그 여부를 받습니다.
        """,
            security = @SecurityRequirement(name = "accessToken")
    )
    @GetMapping("/{postId}")
    public ApiResponse<PostSingleResponse> singlePost(
            @Parameter(description = "조회할 게시글 ID입니다.", schema = @Schema(type = "integer", format = "int64"))
            @PathVariable @ExistPost Long postId,
            @RequestParam(required = false, defaultValue = "false") boolean likeOrScrap
    ) {
        PostSingleResponse response = getPostUseCase.getPostById(postId, likeOrScrap);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시글 페이지 조회 API",
            description = """
        게시글 종류를 받아 페이지 번호와 페이지 크기에 해당하는 게시글을 조회합니다.
        
        게시글 종류는 ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT 중 하나입니다.
        
        페이지 번호는 0부터 시작하며 기본값은 0입니다.
        
        페이지 크기는 1부터 시작하며 기본값은 10입니다.
        """
    )
    @GetMapping
    public ApiResponse<PostPagingResponse> getPagingPost(
            @Parameter(description = "게시글 종류. ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT 중 하나입니다.", required = true, example = "JOB_TALK")
            @RequestParam String type,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @Parameter(description = "페이지 크기 (1부터 시작, 기본값 10)", example = "10")
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PostPagingResponse response = getPostUseCase.getPagingPosts(type, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] Best 인기글 조회",
            description = "Best 인기글을 조회합니다.(인기글 조회시 종류 명시가 필요합니다.)")
    @GetMapping("/best")
    public ApiResponse<PostBest5Response> getPostBest(
            @Parameter(description = "인기글 종류. REAL_TIME, RECOMMEND, COMMENT 중 하나입니다. 요청하지 않으면 기본 값인 REAL_TIME로 조회됩니다.", example = "REAL_TIME")
            @RequestParam(required = false, defaultValue = "REAL_TIME") String sortType
    ) {
        PostBest5Response response = getPostUseCase.getPostBest(sortType);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시판 홈 게시글 조회",
            description = "게시판 홈에 게시글 종류별로 대표1개씩 게시글을 조회합니다.")
    @GetMapping("/representative")
    public ApiResponse<PostRepresentativeResponse> getPostRepresentative() {
        PostRepresentativeResponse response = getPostUseCase.getRepresentativePosts();
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시판 공지 조회",
            description = "공지를 조회합니다.")
    @GetMapping("/announcement")
    public ApiResponse<PostAnnouncementResponse> getPostAnnouncement() {
        PostAnnouncementResponse response = getPostUseCase.getPostAnnouncements();
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시글 스크랩", description = "게시글 스크랩 관련 API")
    @Operation(
            summary = "[마이페이지] 게시글 스크랩 페이지 조회 API",
            description = """
        로그인한 회원이 스크랩한 게시글을 게시글 종류와 페이지 번호, 페이지 크기를 받아 조회합니다.
        
        게시글 종류는 ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT 중 하나입니다.
        
        페이지 번호는 0부터 시작하며 기본값은 0입니다.
        
        페이지 크기는 1부터 시작하며 기본값은 10입니다.
        """
    )
    @GetMapping("/scraps")
    public ApiResponse<PostPagingResponse> getScrapPagingPost(
            @Parameter(description = "게시글 종류. ALL, PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT 중 하나입니다.", required = true, example = "JOB_TALK")
            @RequestParam String type,
            @Parameter(description = "페이지 번호 (0부터 시작, 기본값 0)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @Parameter(description = "페이지 크기 (1부터 시작, 기본값 10)", example = "10")
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PostPagingResponse response = getPostUseCase.getScrapPagingPost(type, pageable);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판 - 댓글", description = "댓글 관련 API")
    @Operation(summary = "!테스트용! 게시글 댓글 조회 API", description = "게시글 ID를 받아 댓글을 조회합니다. 댓글 조회는 이미 게시글 단건 조회에 포함되어 있습니다.")
    @GetMapping("/{postId}/comments")
    public ApiResponse<CommentResponse> getComment(
            @Parameter(
                    description = "조회할 게시글의 ID입니다.",
                    schema = @Schema(type = "integer", format = "int64")
            )
            @PathVariable @ExistPost Long postId
    ) {
        CommentResponse response = getPostUseCase.getCommentsByPostId(postId);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }
}
