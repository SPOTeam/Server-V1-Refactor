package com.example.spot.story.application.port.in.command.impl;

import com.example.spot.member.domain.Member;
import com.example.spot.story.application.port.in.command.LikeStoryUseCase;
import com.example.spot.story.application.port.out.LikeStoryPort;
import com.example.spot.story.domain.entity.LikedStory;
import com.example.spot.story.domain.entity.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeStoryService implements LikeStoryUseCase {

    private final LikeStoryPort likeStoryPort;

    @Override
    public Story likeStory(Story story, Member member) {
        LikedStory likedStory = likeStoryPort.likeStory(story, member);
        story.addLikedPost(likedStory);
        story.plusLikeNum();
        return story;
    }

    @Override
    public Story cancelStoryLike(Story story, Member member) {
        likeStoryPort.cancelStoryLike(story, member);
        return story;
    }
}
