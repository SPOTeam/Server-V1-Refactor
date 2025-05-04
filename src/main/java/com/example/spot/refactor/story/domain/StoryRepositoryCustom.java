package com.example.spot.refactor.story.domain;

import com.example.spot.refactor.story.domain.enums.StoryCategory;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoryRepositoryCustom {

    // 스터디 공지 게시글 페이징 조회
    List<Story> findAnnouncementsByStudyId(Long studyId, Pageable pageable);

    // 테마별 스터디 게시글 페이징 조회
    List<Story> findAllByStudyIdAndTheme(Long studyId, StoryCategory storyCategory, Pageable pageable);

    // 전체 스터디 게시글 페이징 조회
    List<Story> findAllByStudyId(Long studyId, Pageable pageable);
}
