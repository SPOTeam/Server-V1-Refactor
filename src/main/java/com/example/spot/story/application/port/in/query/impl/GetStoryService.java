package com.example.spot.story.application.port.in.query.impl;

import com.example.spot.story.application.port.in.query.GetStoryUseCase;
import com.example.spot.story.application.port.out.GetStoryPort;
import com.example.spot.story.domain.entity.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetStoryService implements GetStoryUseCase {

    private final GetStoryPort getStoryPort;

    @Override
    public Story findStory(Long studyId, Long storyId) {
        return getStoryPort.readStory(studyId, storyId);
    }
}
