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
@RequestMapping("/spot/posts")
public class ManagePostController {

    private final ManagePostUseCase managePostUseCase;

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시글 등록 API",
            description = """
                    입력 받은 값으로 게시글을 하나 등록 합니다. 
                    
                    게시글 종류는 PASS_EXPERIENCE, INFORMATION_SHARING, COUNSELING, JOB_TALK, FREE_TALK, SPOT_ANNOUNCEMENT 중 하나입니다.
                    
                    익명 여부를 선택할 수 있습니다.
                    
                    생성된 게시글의 고유 ID와 게시글 종류, 생성 시간을 반환합니다. 요청 시, 요청 타입은 Multipart/form-data로 보내야 합니다.
                    """,
            security = @SecurityRequirement(name = "accessToken")
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> create(
            @ModelAttribute @Valid PostCreateRequest postCreateRequest
    ) {
        PostCreateResponse response = managePostUseCase.createPost(SecurityUtils.getCurrentUserId(), postCreateRequest);
        return ApiResponse.onSuccess(SuccessStatus._CREATED, response);
    }


    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(
            summary = "[게시판] 게시글 수정 API",
            description =
                    "게시글 Id를 받아 게시글을 수정합니다. existingImage는 기존 이미지 URL입니다. 수정할 이미지가 없을 경우 null로 보내주세요. 요청 시, 요청 타입은 Multipart/form-data로 보내야 합니다."
                            + "\n" + "existingImage와 image 둘 중 하나만 보내주세요. 둘 다 보내면 기존 이미지로 덮어씌워집니다.",
            security = @SecurityRequirement(name = "accessToken")
    )
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostCreateResponse> update(
            @Parameter(
                    description = "수정할 게시글의 ID입니다.",
                    schema = @Schema(type = "integer", format = "int64")
            )
            @PathVariable @ExistPost Long postId,
            @Parameter(
                    description = "수정할 게시글 데이터입니다."
            )
            @ModelAttribute PostUpdateRequest postUpdateRequest
    ) {
        PostCreateResponse response = managePostUseCase.updatePost(SecurityUtils.getCurrentUserId(), postId,
                postUpdateRequest);
        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Tag(name = "게시판", description = "게시판 관련 API")
    @Operation(summary = "[게시판] 게시글 삭제 API", description = "게시글 Id를 받아 게시글을 삭제합니다.")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @Parameter(
                    description = "삭제할 게시글의 ID입니다.",
                    schema = @Schema(type = "integer", format = "int64")
            )
            @PathVariable Long postId
    ) {
        managePostUseCase.deletePost(SecurityUtils.getCurrentUserId(), postId);
        return ApiResponse.onSuccess(SuccessStatus._NO_CONTENT);

    }
}
