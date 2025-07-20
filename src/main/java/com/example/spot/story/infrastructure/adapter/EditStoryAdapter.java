package com.example.spot.story.infrastructure.adapter;

import com.example.spot.common.application.s3.S3ImageService;
import com.example.spot.member.domain.Member;
import com.example.spot.report.domain.StoryReportRepository;
import com.example.spot.story.application.port.out.EditStoryPort;
import com.example.spot.story.domain.dto.request.StoryRequestDTO;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.domain.entity.StoryImage;
import com.example.spot.story.infrastructure.repository.LikedStoryRepository;
import com.example.spot.story.infrastructure.repository.StoryCommentRepository;
import com.example.spot.story.infrastructure.repository.StoryImageRepository;
import com.example.spot.story.infrastructure.repository.StoryRepository;
import com.example.spot.study.domain.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EditStoryAdapter implements EditStoryPort {

    private final StoryRepository storyRepository;
    private final StoryImageRepository storyImageRepository;
    private final StoryCommentRepository storyCommentRepository;
    private final LikedStoryRepository likedStoryRepository;
    private final StoryReportRepository storyReportRepository;

    private final S3ImageService s3ImageService;

    @Override
    public Story createStory(StoryRequestDTO.StoryDTO storyDTO, Member member, Study study) {

        Story story = storyRepository.save(Story.of(member, study, storyDTO.getIsAnnouncement(),
                storyDTO.getStoryCategory(), storyDTO.getTitle(), storyDTO.getContent()));

        // 이미지가 있는 경우 이미지 저장
        if (storyDTO.getImage() != null) {
            createStoryImage(storyDTO.getImage(), story);
        }
        return story;
    }

    @Override
    public Story deleteStory(Story story) {
        storyImageRepository.deleteAllByStoryId(story.getId());
        storyCommentRepository.deleteAllByStoryId(story.getId());
        likedStoryRepository.deleteAllByStoryId(story.getId());
        storyReportRepository.deleteAllByStoryId(story.getId());

        storyRepository.delete(story);
        return story;
    }

    private void createStoryImage(MultipartFile image, Story savedStory) {
        String imageUrl = s3ImageService.upload(image);
        StoryImage storyImage = storyImageRepository.save(StoryImage.of(imageUrl, savedStory));
        savedStory.addImage(storyImage);
    }

}
