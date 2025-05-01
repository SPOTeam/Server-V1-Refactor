package com.example.spot.legacy.web.dto.search;

import com.example.spot.refactor.study.domain.enums.ThemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SearchRequestStudyWithThemeDTO extends BaseSearchRequestStudyDTO {

    @Schema(description = "스터디 테마 리스트입니다. (예: HOBBY, PROJECT, EXAM)", example = "[\"HOBBY\", \"PROJECT\"]")
    private List<ThemeType> themeTypes;
}
