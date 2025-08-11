package com.example.spot.post.infrastructure.jpa.schedule;

import com.example.spot.post.domain.schedule.PostScheduleLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScheduleLikesRepository extends JpaRepository<PostScheduleLikes, Long> {
}
