package com.example.spot.legacy.repository;

import com.example.spot.refactor.post.domain.schedule.PostScheduleRealTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScheduleRealTimeRepository extends JpaRepository<PostScheduleRealTime, Long> {
}
