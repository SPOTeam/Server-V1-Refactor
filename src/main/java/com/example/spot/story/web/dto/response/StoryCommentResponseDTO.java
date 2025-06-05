package com.example.spot.story.web.dto.response;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.association.LikedStoryComment;
import com.example.spot.story.domain.association.StoryComment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Getter
public class StoryCommentResponseDTO {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class CommentDTO {

        private final Long commentId;
        private final CommentingMemberDTO member;
        private final String content;
        private final Integer likeCount;
        private final Integer dislikeCount;

        public static CommentDTO toDTO(StoryComment comment, String name, String defaultImage) {
            return CommentDTO.builder()
                    .commentId(comment.getId())
                    .member(CommentingMemberDTO.toDTO(comment.getMember(), name, comment.getIsAnonymous(), defaultImage))
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .dislikeCount(comment.getDislikeCount())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class CommentingMemberDTO {

        private final Long memberId;
        private final String name;
        private final String profileImage;

        public static CommentingMemberDTO toDTO(Member member, String anonymity, Boolean isAnonymous, String defaultImage) {
            if (isAnonymous) {
                return CommentingMemberDTO.builder()
                        .memberId(member.getId())
                        .name(anonymity)
                        .profileImage(defaultImage)
                        .build();
            } else {
                return CommentingMemberDTO.builder()
                        .memberId(member.getId())
                        .name(member.getName())
                        .profileImage(member.getProfileImage())
                        .build();
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class CommentIdDTO {
        private final Long commentId;
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class CommentPreviewDTO {

        private final Long commentId;
        private final Integer likeCount;
        private final Integer dislikeCount;

        public static CommentPreviewDTO toDTO(StoryComment comment) {
            return CommentPreviewDTO.builder()
                    .commentId(comment.getId())
                    .likeCount(comment.getLikeCount())
                    .dislikeCount(comment.getDislikeCount())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ReplyListDTO {

        private final Long postId;
        private final List<ReplyDTO> comments;

        public static ReplyListDTO toDTO(Long postId, List<StoryComment> comments, Member member, String defaultImage) {
            return ReplyListDTO.builder()
                    .postId(postId)
                    .comments(comments.stream()
                            .map(comment -> ReplyDTO.toDTO(comment, member, defaultImage))
                            .toList())
                    .build();
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder(access = AccessLevel.PRIVATE)
    public static class ReplyDTO {

        private final Long commentId;
        private final CommentingMemberDTO member;
        private final String content;
        private final Integer likeCount;
        private final Integer dislikeCount;
        private final Boolean isDeleted;
        private final String isLiked;
        private final List<ReplyDTO> applies;

        public static ReplyDTO toDTO(StoryComment comment, Member member, String defaultImage) {

            String anonymity = "익명" + comment.getAnonymousNum();
            return ReplyDTO.builder()
                    .commentId(comment.getId())
                    .member(CommentingMemberDTO.toDTO(comment.getMember(), anonymity, comment.getIsAnonymous(), defaultImage))
                    .content(comment.getContent())
                    .likeCount(comment.getLikeCount())
                    .dislikeCount(comment.getDislikeCount())
                    .isDeleted(comment.getIsDeleted())
                    .isLiked(getIsLiked(comment, member))
                    .applies(comment.getChildrenComment().stream()
                            .sorted(Comparator.comparing(StoryComment::getCreatedAt))
                            .map(child -> ReplyDTO.toDTO(child, member, defaultImage))
                            .toList())
                    .build();
        }

        private static String getIsLiked(StoryComment comment, Member member) {
            String isLiked = "NONE";
            for (LikedStoryComment likedComment : comment.getLikedComments()) {
                if (likedComment.getMember().equals(member)) {
                    if (likedComment.getIsLiked()) {
                        isLiked = "LIKED";
                    } else {
                        isLiked = "DISLIKED";
                    }
                    break;
                }
            }
            return isLiked;
        }
    }
}
