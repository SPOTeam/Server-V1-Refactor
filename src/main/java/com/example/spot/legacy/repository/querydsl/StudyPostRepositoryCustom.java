package com.example.spot.legacy.repository.querydsl;

import com.example.spot.legacy.domain.enums.Theme;
import com.example.spot.legacy.domain.study.StudyPost;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyPostRepositoryCustom {

    // 스터디 공지 게시글 페이징 조회
    List<StudyPost> findAnnouncementsByStudyId(Long studyId, Pageable pageable);

    // 테마별 스터디 게시글 페이징 조회
    List<StudyPost> findAllByStudyIdAndTheme(Long studyId, Theme theme, Pageable pageable);

    // 전체 스터디 게시글 페이징 조회
    List<StudyPost> findAllByStudyId(Long studyId, Pageable pageable);
}
