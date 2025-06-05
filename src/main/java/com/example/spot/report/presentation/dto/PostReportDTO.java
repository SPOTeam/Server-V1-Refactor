package com.example.spot.report.presentation.dto;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostReportDTO {
    private Long reportedPostId;
    private Long reporterId;
    private LocalDateTime reportedAt;

    public static PostReportDTO toDTO(Long reportedPostId, Long reporterId) {
        return PostReportDTO.builder()
                .reportedPostId(reportedPostId)
                .reporterId(reporterId)
                .reportedAt(LocalDateTime.now())
                .build();
    }
}
