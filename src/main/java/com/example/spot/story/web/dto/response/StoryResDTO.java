package com.example.spot.story.web.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.Story;
import com.example.spot.story.domain.enums.StoryCategory;
import com.example.spot.story.domain.association.StoryImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StoryResDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class PostPreviewDTO {

        private final Long postId;
        private final String title;

        public static PostPreviewDTO toDTO(Story story) {
            return PostPreviewDTO.builder()
                    .postId(story.getId())
                    .title(story.getTitle())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ImageListDTO {
        private final List<String> images;
    }

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class PostListDTO {

        private final Long studyId;
        private final List<PostDTO> posts;
        private final Long totalPages;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class PostDTO {

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

        public static PostDTO toDTO(Story story, boolean isLiked) {
            return PostDTO.builder()
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
    public static class PostDetailDTO {

        private final PostMemberDTO member;
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

        public static PostDetailDTO toDTO(Story story, Integer commentNum, boolean isLiked, boolean isWriter) {
            return PostDetailDTO.builder()
                    .member(PostMemberDTO.toDTO(story.getMember()))
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
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    private static class PostMemberDTO {

        private final Long memberId;
        private final String name;
        private final String profileImage;

        public static PostMemberDTO toDTO(Member member) {
            return PostMemberDTO.builder()
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
    public static  class PostLikeNumDTO {

        private final Long postId;
        private final String title;
        private final Integer likeNum;

        public static PostLikeNumDTO toDTO(Story story) {
            return PostLikeNumDTO.builder()
                    .postId(story.getId())
                    .title(story.getTitle())
                    .likeNum(story.getLikeNum())
                    .build();
        }

    }
}
