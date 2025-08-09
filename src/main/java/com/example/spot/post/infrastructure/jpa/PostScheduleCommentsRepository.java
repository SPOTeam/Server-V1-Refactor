package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.schedule.PostScheduleComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScheduleCommentsRepository extends JpaRepository<PostScheduleComments, Long> {
}
