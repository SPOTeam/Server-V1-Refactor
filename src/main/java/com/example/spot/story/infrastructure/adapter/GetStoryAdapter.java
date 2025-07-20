package com.example.spot.story.infrastructure.adapter;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.story.application.port.out.GetStoryPort;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStoryAdapter implements GetStoryPort {

    private final StoryRepository storyRepository;

    @Override
    public Story readStory(Long studyId, Long storyId) {
        return storyRepository.findByIdAndStudyId(storyId, studyId)
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_POST_NOT_FOUND));
    }
}
