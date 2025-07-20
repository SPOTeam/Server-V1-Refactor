package com.example.spot.story.application.port.in.command;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.study.domain.Study;

public interface EditStoryUseCase {

    Story createStory(StoryRequestDTO.StoryDTO storyDTO, Member member, Study study);

    Story updateStory(StoryRequestDTO.StoryDTO storyDTO, Story story);

    Story deleteStory(Study study, Story story);
}
