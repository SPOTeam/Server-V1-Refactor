package com.example.spot.post.infrastructure.jpa;

import com.example.spot.post.domain.schedule.PostScheduleRealTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScheduleRealTimeRepository extends JpaRepository<PostScheduleRealTime, Long> {
}
