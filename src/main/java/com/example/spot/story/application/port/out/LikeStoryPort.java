package com.example.spot.story.application.port.out;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.entity.LikedStory;
import com.example.spot.story.domain.entity.Story;

public interface LikeStoryPort {

    LikedStory likeStory(Story story, Member member);

    void cancelStoryLike(Story story, Member member);
}
