package com.example.spot.post.presentation.dto.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapAllDeleteRequest {

    @Schema(description = "삭제할 게시글 Id 리스트 입니다.")
    private List<Long> deletePostIds;
}
