package com.example.spot.legacy.repository;

import com.example.spot.refactor.post.domain.schedule.PostScheduleComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScheduleCommentsRepository extends JpaRepository<PostScheduleComments, Long> {
}
