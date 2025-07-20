package com.example.spot.story.application.port.in.command;

import com.example.spot.member.domain.Member;
import com.example.spot.story.domain.entity.Story;

public interface LikeStoryUseCase {

    Story likeStory(Story story, Member member);

    Story cancelStoryLike(Story story, Member member);


}
