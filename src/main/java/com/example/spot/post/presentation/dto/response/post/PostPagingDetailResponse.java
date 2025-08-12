package com.example.spot.post.presentation.dto.response.post;

import com.example.spot.post.domain.Post;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostPagingDetailResponse {

    private Long postId;
    private String writer;
    private LocalDateTime writtenTime;
    private Long scrapCount;
    private String title;
    private String content;
    private Long likeCount;
    private int commentCount;
    private int viewCount;
    private boolean likedByCurrentUser;
    private boolean scrapedByCurrentUser;

    public static String judgeAnonymous(Boolean isAnonymous, String writer) {

        if (isAnonymous) {
            return "익명";
        }
        return writer;
    }

    public static PostPagingDetailResponse toDTO(Post post, long likeCount, long scrapCount, boolean likedByCurrentUser,
                                                 boolean scrapedByCurrentUser) {
        // 작성자가 익명인지 확인하여 작성자 이름 설정
        String writerName = judgeAnonymous(post.isAnonymous(), post.getMember().getName());

        return PostPagingDetailResponse.builder()
                .postId(post.getId())
                .writer(writerName)
                .writtenTime(post.getCreatedAt())
                .scrapCount(scrapCount)
                .scrapedByCurrentUser(scrapedByCurrentUser)
                .title(post.getTitle())
                .content(post.getContent())
                .likeCount(likeCount)
                .likedByCurrentUser(likedByCurrentUser)
                .commentCount(post.getPostCommentList().size())
                .viewCount(0)
                .build();
    }
}
