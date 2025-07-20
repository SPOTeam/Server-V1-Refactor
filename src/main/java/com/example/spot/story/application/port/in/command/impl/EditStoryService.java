package com.example.spot.story.application.port.in.command.impl;

import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.member.domain.Member;
import com.example.spot.story.application.port.in.command.EditStoryUseCase;
import com.example.spot.story.application.port.out.EditStoryPort;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.domain.entity.StoryImage;
import com.example.spot.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EditStoryService implements EditStoryUseCase {

    private final EditStoryPort editStoryPort;
    private final S3ImageService s3ImageService;

    @Override
    public Story createStory(StoryRequestDTO.StoryDTO storyDTO, Member member, Study study) {
        Story story = editStoryPort.createStory(storyDTO, member, study);
        study.addStory(story);
        return story;
    }

    @Override
    public Story updateStory(StoryRequestDTO.StoryDTO storyDTO, Story story) {
        updateStoryImages(storyDTO, story);
        story.updatePost(storyDTO);
        return story;
    }

    @Override
    public Story deleteStory(Study study, Story story) {
        study.deleteStudyPost(story);
        return editStoryPort.deleteStory(story);
    }

    private void updateStoryImages(StoryRequestDTO.StoryDTO storyDTO, Story story) {
        List<StoryImage> storyImages = story.getImages();
        // 기존 이미지가 존재하는 경우 이미지 유지
        if (!StringUtils.hasText(storyDTO.getExistingImage())) {
            // 기존 이미지가 없고 새로운 이미지를 등록한 경우 이미지 url 변경
            if (storyDTO.getImage() != null) {
                String imageUrl = s3ImageService.upload(storyDTO.getImage());
                storyImages.forEach(studyPostImage -> studyPostImage.setUrl(imageUrl));
            }
        }
    }

}
