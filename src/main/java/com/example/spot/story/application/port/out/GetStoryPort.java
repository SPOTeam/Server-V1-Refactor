package com.example.spot.story.application.port.out;

import com.example.spot.story.domain.entity.Story;

public interface GetStoryPort {

    // 스토리 조회
    Story readStory(Long studyId, Long storyId);
}
