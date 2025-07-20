package com.example.spot.story.application.port.out;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.study.domain.Study;

public interface EditStoryPort {

    // 스토리 생성
    Story createStory(StoryRequestDTO.StoryDTO storyDTO, Member member, Study study);

    // 스토리 삭제
    Story deleteStory(Story story);
}
