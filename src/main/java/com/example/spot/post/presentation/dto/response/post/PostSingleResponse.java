package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.enums.Board;
import com.example.spot.post.presentation.dto.response.comment.CommentResponse;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostSingleResponse {

    private String type;
    private String writer;
    private Boolean anonymous;
    private String profileImage;
    private LocalDateTime writtenTime;

    private String title;
    private String content;
    private String imageUrl;

    private Long scrapCount;
    private Long likeCount;
    private Integer commentCount;
    private Integer viewCount;

    private Boolean likedByCurrentUser;
    private Boolean scrapedByCurrentUser;
    private Boolean createdByCurrentUser;
    private CommentResponse commentResponses;
    private boolean isReported;

    public Board getType() {
        return Board.findByValue(type);
    }

    public static String judgeAnonymous(Boolean isAnonymous, String writer) {

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

    public static PostSingleResponse toDTO(Post post, long likeCount, long scrapCount, CommentResponse commentResponse,
                                           boolean likedByCurrentUser, boolean scrapedByCurrentUser,
                                           boolean createdByCurrentUser, String defaultProfileImageUrl) {
        // 작성자가 익명인지 확인하여 작성자 이름 설정
        String writerName = judgeAnonymous(post.isAnonymous(), post.getMember().getNickname());
        // 작성자가 익명인지 확인하여 프로필 반환
        String writerImage = anonymousProfileImage(post.isAnonymous(), post.getMember().getProfileImage(),
                defaultProfileImageUrl);

        return PostSingleResponse.builder()
                .type(post.getBoard().name())
                .writer(writerName)
                .anonymous(post.isAnonymous())
                .profileImage(writerImage)
                .writtenTime(post.getCreatedAt())
                .scrapCount(scrapCount)
                .scrapedByCurrentUser(scrapedByCurrentUser)
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(likeCount)
                .imageUrl(post.getImage())
                .likedByCurrentUser(likedByCurrentUser)
                .createdByCurrentUser(createdByCurrentUser)
                .commentCount(commentResponse.getComments().size())
                .viewCount(0)
                .commentResponses(commentResponse)
                .build();
    }
}
