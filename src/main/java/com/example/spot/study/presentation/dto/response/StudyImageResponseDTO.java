package com.example.spot.study.presentation.dto.response;

import com.example.spot.story.domain.association.StoryImage;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class StudyImageResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ImageListDTO {

        private final Long studyId;
        private final List<ImageDTO> images;

        public static ImageListDTO toDTO(Long studyId, List<ImageDTO> images) {
            return ImageListDTO.builder()
                    .studyId(studyId)
                    .images(images)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ImageDTO {

        private final Long imageId;
        private final String imageUrl;
        private final Long postId;

        public static ImageDTO toDTO(StoryImage storyImage) {
            return ImageDTO.builder()
                    .postId(storyImage.getStory().getId())
                    .imageId(storyImage.getId())
                    .imageUrl(storyImage.getUrl())
                    .build();
        }
    }
}
