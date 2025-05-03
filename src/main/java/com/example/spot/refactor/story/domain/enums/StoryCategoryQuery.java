package com.example.spot.refactor.story.domain.enums;

import com.example.spot.refactor.common.api.code.status.ErrorStatus;
import com.example.spot.refactor.common.api.exception.handler.StudyHandler;

public enum StoryCategoryQuery {

    ANNOUNCEMENT, WELCOME, INFO_SHARING, STUDY_REVIEW, FREE_TALK, QNA

    ;

    public StoryCategory toCategory() {
        return switch (this) {
            case WELCOME -> StoryCategory.WELCOME;
            case INFO_SHARING -> StoryCategory.INFO_SHARING;
            case STUDY_REVIEW -> StoryCategory.STUDY_REVIEW;
            case FREE_TALK -> StoryCategory.FREE_TALK;
            case QNA -> StoryCategory.QNA;
            default -> throw new StudyHandler(ErrorStatus._THEME_NOT_FOUND);
        };
    }
}
