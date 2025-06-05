package com.example.spot.report.presentation.dto;

import com.example.spot.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudyMemberReportDTO {

    @TextLength(min = 1, max = 255)
    private String content;
}
