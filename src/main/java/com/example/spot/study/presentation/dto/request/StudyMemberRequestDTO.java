package com.example.spot.study.presentation.dto.request;

import com.example.spot.member.domain.enums.Gender;
import com.example.spot.study.domain.enums.ThemeType;
import com.example.spot.common.presentation.validator.IntSize;
import com.example.spot.common.presentation.validator.LongSize;
import com.example.spot.common.presentation.validator.TextLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class StudyMemberRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterDTO {

        private List<ThemeType> themes;

        @TextLength(min = 1, max = 255)
        private String title;

        @TextLength(min = 1, max = 255)
        private String goal;

        @TextLength(min = 1, max = 255)
        private String introduction;

        private Boolean isOnline;

        @TextLength(min = 1, max = 255)
        private String profileImage;

        private List<String> regions;

        @LongSize(min = 2)
        private Long maxPeople;

        private Gender gender;

        @IntSize(min = 1)
        private Integer minAge;

        @IntSize(min = 1)
        private Integer maxAge;

        private Integer fee;

        private boolean hasFee;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinDTO {
        @TextLength(min = 1, max = 255)
        private String introduction;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HostWithdrawDTO {
        private Long newHostId;
        private String reason;
    }

}
