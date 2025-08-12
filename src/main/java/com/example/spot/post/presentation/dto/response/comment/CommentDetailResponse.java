package com.example.spot.post.presentation.dto.response.comment;

import com.example.spot.post.domain.PostComment;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentDetailResponse {

    private Long commentId;
    private String commentContent;
    private String writer;
    private boolean anonymous;
    private String profileImage;
    private String writtenTime;

    public static CommentDetailResponse toDTO(PostComment comment, long likeCount, boolean likedByCurrentUser,
                                              boolean dislikedByCurrentUser, String defaultProfileImageUrl) {
        // 작성자가 익명인지 확인하여 작성자 이름 설정
        String writerName = judgeAnonymous(comment.isAnonymous(), comment.getMember().getName());
        // 작성자가 익명인지 확인하여 프로필 반환
        String writerImage = anonymousProfileImage(comment.isAnonymous(), comment.getMember().getProfileImage(),
                defaultProfileImageUrl);

        return CommentDetailResponse.builder()
                .commentId(comment.getId())
                .profileImage(writerImage)
                .commentContent(comment.getContent())
                .writer(writerName)
                .anonymous(comment.isAnonymous())
                .writtenTime(comment.getCreatedAt() != null ? comment.getCreatedAt().toString()
                        : LocalDateTime.now().toString())
                .build();
    }

    static String judgeAnonymous(Boolean isAnonymous, String writer) {
        if (isAnonymous) {
            return "익명";
        }
        return writer;
    }

    public static String anonymousProfileImage(Boolean isAnonymous, String profileImage,
                                               String defaultProfileImageUrl) {
        if (isAnonymous) {
            return defaultProfileImageUrl;
        }
        return profileImage;
    }
}
