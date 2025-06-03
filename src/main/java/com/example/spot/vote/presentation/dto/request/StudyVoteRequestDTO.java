package com.example.spot.vote.presentation.dto.request;

import com.example.spot.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class StudyVoteRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteDTO {

        @TextLength(min = 1, max = 255)
        private String title;

        private List<String> options;
        private Boolean isMultipleChoice;
        private LocalDateTime finishedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VotedOptionDTO {

        private List<Long> optionIdList;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoteUpdateDTO {

        @TextLength(min = 1, max = 255)
        private String title;

        private List<OptionDTO> options;
        private Boolean isMultipleChoice;
        private LocalDateTime finishedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionDTO {

        @TextLength(min = 1)
        private Long optionId;

        @TextLength(min = 1, max = 255)
        private String content;
    }

}
