package com.example.spot.story.application.port.in.query;

import com.example.spot.story.domain.entity.Story;

public interface GetStoryUseCase {

    Story findStory(Long studyId, Long storyId);
}
