package com.example.spot.story.domain.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.domain.entity.StoryImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StoryResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class StoryPreviewDTO {

        private final Long postId;
        private final String title;

        public static StoryPreviewDTO toDTO(Story story) {
            return StoryPreviewDTO.builder()
                    .postId(story.getId())
                    .title(story.getTitle())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class StoryListDTO {

        private final Long studyId;
        private final List<StoryDTO> posts;
        private final Long totalPages;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class StoryDTO {

        private final Long postId;
        private final String title;
        private final String content;
        private final StoryCategory storyCategory;
        private final Boolean isAnnouncement;
        private final LocalDateTime createdAt;
        private final Integer likeNum;
        private final Integer hitNum;
        private final Integer commentNum;
        private final Boolean isLiked;

        public static StoryDTO toDTO(Story story, boolean isLiked) {
            return StoryDTO.builder()
                    .postId(story.getId())
                    .title(story.getTitle())
                    .content(story.getContent())
                    .storyCategory(story.getStoryCategory())
                    .isAnnouncement(story.getIsAnnouncement())
                    .createdAt(story.getCreatedAt())
                    .likeNum(story.getLikeNum())
                    .hitNum(story.getHitNum())
                    .commentNum(story.getCommentNum())
                    .isLiked(isLiked)
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class StoryDetailDTO {

        private final PostingMemberDTO member;
        private final Long postId;
        private final String title;
        private final String content;
        private final StoryCategory storyCategory;
        private final Boolean isAnnouncement;
        private final LocalDateTime createdAt;
        private final Integer likeNum;
        private final Integer hitNum;
        private final Integer commentNum;
        private final Boolean isLiked;
        private final Boolean isWriter;
        private final List<ImageDTO> studyPostImages;

        public static StoryDetailDTO toDTO(Story story, Integer commentNum, boolean isLiked, boolean isWriter) {
            return StoryDetailDTO.builder()
                    .member(PostingMemberDTO.toDTO(story.getMember()))
                    .postId(story.getId())
                    .title(story.getTitle())
                    .content(story.getContent())
                    .storyCategory(story.getStoryCategory())
                    .isAnnouncement(story.getIsAnnouncement())
                    .createdAt(story.getCreatedAt())
                    .likeNum(story.getLikeNum())
                    .hitNum(story.getHitNum())
                    .commentNum(commentNum)
                    .isLiked(isLiked)
                    .isWriter(isWriter)
                    .studyPostImages(story.getImages().stream()
                            .map(ImageDTO::toDTO)
                            .toList())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class StoryContentDTO {
        private final String title;
        private final String content;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class PostingMemberDTO {

        private final Long memberId;
        private final String name;
        private final String profileImage;

        public static PostingMemberDTO toDTO(Member member) {
            return PostingMemberDTO.builder()
                    .memberId(member.getId())
                    .name(member.getName())
                    .profileImage(member.getProfileImage())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ImageDTO {

        private final Long imageId;
        private final String imageUrl;

        public static ImageDTO toDTO(StoryImage storyImage) {
            return ImageDTO.builder()
                    .imageId(storyImage.getId())
                    .imageUrl(storyImage.getUrl())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static  class StoryLikeNumDTO {

        private final Long postId;
        private final String title;
        private final Integer likeNum;

        public static StoryLikeNumDTO toDTO(Story story) {
            return StoryLikeNumDTO.builder()
                    .postId(story.getId())
                    .title(story.getTitle())
                    .likeNum(story.getLikeNum())
                    .build();
        }
    }
}
