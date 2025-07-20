package com.example.spot.story.infrastructure.adapter;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.handler.StudyHandler;
import com.example.spot.member.domain.Member;
import com.example.spot.story.application.port.out.LikeStoryPort;
import com.example.spot.story.domain.entity.LikedStory;
import com.example.spot.story.domain.entity.Story;
import com.example.spot.story.infrastructure.repository.LikedStoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeStoryAdapter implements LikeStoryPort {

    private final LikedStoryRepository likedStoryRepository;


    @Override
    public LikedStory likeStory(Story story, Member member) {
        return likedStoryRepository.save(LikedStory.of(story, member));
    }

    @Override
    public void cancelStoryLike(Story story, Member member) {
        LikedStory likedStory = likedStoryRepository.findByMemberIdAndStoryId(member.getId(), story.getId())
                .orElseThrow(() -> new StudyHandler(ErrorStatus._STUDY_LIKED_POST_NOT_FOUND));
        story.deleteLikedPost(likedStory);
        story.minusLikeNum();
        likedStoryRepository.delete(likedStory);
    }
}
