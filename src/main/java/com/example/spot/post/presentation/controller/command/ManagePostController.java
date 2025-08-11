package com.example.spot.post.presentation.controller.command;

import com.example.spot.common.api.ApiResponse;
import com.example.spot.common.api.code.status.SuccessStatus;
import com.example.spot.common.security.utils.SecurityUtils;
import com.example.spot.post.application.command.ManagePostUseCase;
import com.example.spot.post.presentation.dto.request.post.PostCreateRequest;
import com.example.spot.post.presentation.dto.request.post.PostUpdateRequest;
import com.example.spot.post.presentation.dto.response.post.PostCreateResponse;
import com.example.spot.post.presentation.validator.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/spot/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "게시판", description = "게시글 생성/수정/삭제 API")
@SecurityRequirement(name = "accessToken")
public class ManagePostController {

    private final ManagePostUseCase managePostUseCase;

    @Operation(
            summary = "[게시판] 게시글 등록",
            description = """
                    게시글을 등록합니다. 요청은 multipart/form-data 이어야 합니다.
                    - 유형: PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT
                    - 익명 여부 선택 가능
                    반환: 생성된 게시글 ID/유형/생성시간
                    """
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> create(
            @Parameter(description = "등록 요청 데이터")
            @ModelAttribute @Valid PostCreateRequest postCreateRequest
    ) {
        Long memberId = SecurityUtils.getCurrentUserId();
        PostCreateResponse response = managePostUseCase.createPost(memberId, postCreateRequest);
        return ApiResponse.onSuccess(SuccessStatus._CREATED, response);
    }

    @Operation(
            summary = "[게시판] 게시글 수정",
            description = """
                    게시글을 수정합니다. 요청은 multipart/form-data 이어야 합니다.
                    - existingImage: 기존 이미지 URL (없으면 null)
                    - image와 existingImage 중 하나만 전송 (둘 다 전송 시 기존 이미지로 덮어쓰기)
                    """
    )
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> update(
            @Parameter(description = "게시글 ID", schema = @Schema(type = "integer", format = "int64"))
            @PathVariable @ExistPost Long postId,
            @Parameter(description = "수정 요청 데이터")
            @ModelAttribute @Valid PostUpdateRequest postUpdateRequest
    ) {
        Long memberId = SecurityUtils.getCurrentUserId();
        PostCreateResponse response = managePostUseCase.updatePost(memberId, postId, postUpdateRequest);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Operation(summary = "[게시판] 게시글 삭제", description = "지정한 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @Parameter(description = "게시글 ID", schema = @Schema(type = "integer", format = "int64"))
            @PathVariable @ExistPost Long postId
    ) {
        Long memberId = SecurityUtils.getCurrentUserId();
        managePostUseCase.deletePost(memberId, postId);
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT);
    }
}