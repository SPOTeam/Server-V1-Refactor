package com.example.spot.refactor.study.domain.aggregate.studypost;

import com.example.spot.refactor.study.domain.enums.StudyPostCategory;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyPostRepositoryCustom {

    // 스터디 공지 게시글 페이징 조회
    List<StudyPost> findAnnouncementsByStudyId(Long studyId, Pageable pageable);

    // 테마별 스터디 게시글 페이징 조회
    List<StudyPost> findAllByStudyIdAndTheme(Long studyId, StudyPostCategory studyPostCategory, Pageable pageable);

    // 전체 스터디 게시글 페이징 조회
    List<StudyPost> findAllByStudyId(Long studyId, Pageable pageable);
}
