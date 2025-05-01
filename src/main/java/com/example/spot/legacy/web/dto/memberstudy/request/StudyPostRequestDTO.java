package com.example.spot.legacy.web.dto.memberstudy.request;

import com.example.spot.refactor.study.domain.enums.StudyPostCategory;
import com.example.spot.legacy.validation.annotation.TextLength;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class StudyPostRequestDTO {

    @Getter
    @Setter
    @Builder
    @Schema(name = "StudyPostDTO")
    @AllArgsConstructor
    public static class PostDTO {

        @NotNull
        @Schema(description = "공지 여부", example = "false")
        private Boolean isAnnouncement;

        @NotNull
        @Schema(description = "테마", example = "WELCOME")
        private StudyPostCategory studyPostCategory;

        @NotNull
        @TextLength(min = 1, max = 50)
        @Schema(description = "제목", example = "title")
        private String title;

        @NotNull
        @TextLength(min = 1, max = 255)
        @Schema(description = "내용", example = "content")
        private String content;

        @Schema(description = "신규 이미지")
        private MultipartFile image;

        @Schema(description = "기존 이미지")
        private String existingImage;

    }
}
