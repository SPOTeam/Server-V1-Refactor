package com.example.spot.common.scheduler;

import com.example.spot.post.domain.Post;
import com.example.spot.post.domain.schedule.PostScheduleComments;
import com.example.spot.post.infrastructure.jpa.PostRepository;
import com.example.spot.post.infrastructure.jpa.PostScheduleCommentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostSortCommentsScheduler {

    private final PostRepository postRepository;
    private final PostScheduleCommentsRepository postScheduleRepository;

    @Transactional
    @Scheduled(cron = "0 0 13,18 * * ?", zone = "Asia/Seoul")
    public void generatePostSortComments() {
        List<Post> topByOrderByCommentPosts = postRepository.findTopByOrderByCommentCountDesc();
        List<PostScheduleComments> postScheduleCommentList = new ArrayList<>();

        int size = Math.min(topByOrderByCommentPosts.size(), 5); // 리스트 크기와 5 중 작은 값을 선택

        for (int i = 0; i < size; i++) {
            Post post = topByOrderByCommentPosts.get(i);
            PostScheduleComments postScheduleComments = PostScheduleComments.of(post, i + 1);
            postScheduleCommentList.add(postScheduleComments);
        }

        postScheduleRepository.saveAll(postScheduleCommentList);
    }
}
