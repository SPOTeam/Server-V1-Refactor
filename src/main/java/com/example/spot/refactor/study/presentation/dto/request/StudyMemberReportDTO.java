package com.example.spot.refactor.study.presentation.dto.request;

import com.example.spot.refactor.common.presentation.validator.TextLength;
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
