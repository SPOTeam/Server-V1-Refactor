package com.example.spot.refactor.story.domain.enums;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.common.api.exception.handler.StudyHandler;

public enum StudyPostCategoryQuery {

    ANNOUNCEMENT, WELCOME, INFO_SHARING, STUDY_REVIEW, FREE_TALK, QNA

    ;

    public StudyPostCategory toCategory() {
        return switch (this) {
            case WELCOME -> StudyPostCategory.WELCOME;
            case INFO_SHARING -> StudyPostCategory.INFO_SHARING;
            case STUDY_REVIEW -> StudyPostCategory.STUDY_REVIEW;
            case FREE_TALK -> StudyPostCategory.FREE_TALK;
            case QNA -> StudyPostCategory.QNA;
            default -> throw new StudyHandler(ErrorStatus._THEME_NOT_FOUND);
        };
    }
}
