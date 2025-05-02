package com.example.spot.refactor.study.domain.repository;

import com.example.spot.refactor.member.domain.enums.Status;
import com.example.spot.refactor.study.domain.aggregate.studymember.StudyMember;
import com.example.spot.refactor.study.domain.enums.StudySortBy;
import com.example.spot.refactor.study.domain.aggregate.studyregion.StudyRegion;
import com.example.spot.refactor.study.domain.aggregate.studytheme.StudyTheme;
import com.example.spot.refactor.study.domain.aggregate.Study;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

public interface StudyRepositoryCustom {
    List<Study> searchByTitle(String keyword, StudySortBy sortBy, Pageable pageable);
    List<Study> findAllStudyByConditions(Map<String, Object> search, StudySortBy sortBy, Pageable pageable);
    List<Study> findAllStudy(StudySortBy sortBy, Pageable pageable);

    List<Study> findByStudyTheme(List<StudyTheme> studyThemes);

    List<Study> findByStudyThemeAndNotInIds(List<StudyTheme> studyThemes, List<Long> studyIds);
    List<Study> findByRegionStudyAndNotInIds(List<StudyRegion> regionStudies, List<Long> studyIds);

    // 모집중 스터디 조회
    List<Study> findRecruitingStudyByConditions(Map<String, Object> search, StudySortBy sortBy, Pageable pageable);

    List<Study> findStudyByConditionsAndThemeTypesAndNotInIds(Map<String, Object> search,
        StudySortBy sortBy, Pageable pageable, List<StudyTheme> themeTypes, List<Long> studyIds);

    List<Study> findStudyByConditionsAndRegionStudiesAndNotInIds(Map<String, Object> search,
                                                                 StudySortBy sortBy, Pageable pageable, List<StudyRegion> regionStudies, List<Long> studyIds);

    List<Study> findAllByTitleContaining(String title, StudySortBy sortBy, Pageable pageable);

    List<Study> findByStudyTheme(List<StudyTheme> studyTheme, StudySortBy sortBy, Pageable pageable);

    List<Study> findByMemberStudy(List<StudyMember> studyMember, Pageable pageable);
    List<Study> findRecruitingStudiesByMemberStudy(List<StudyMember> studyMember, Pageable pageable);

    long countStudyByConditionsAndThemeTypesAndNotInIds(
        Map<String, Object> search, List<StudyTheme> themeTypes, StudySortBy sortBy, List<Long> studyIds);

    long countStudyByConditionsAndRegionStudiesAndNotInIds(
            Map<String, Object> search, List<StudyRegion> regionStudies, StudySortBy sortBy, List<Long> studyIds);
    long countStudyByConditions(Map<String, Object> search, StudySortBy sortBy);
    long countStudyByStudyTheme(List<StudyTheme> studyThemes, StudySortBy sortBy);

    long countAllByTitleContaining(String title, StudySortBy sortBy);

    long countByMemberStudiesAndStatus(List<StudyMember> memberStudies, Status status);
    long countByMemberStudiesAndStatusAndIsOwned(List<StudyMember> memberStudies, Status status, boolean isOwned);

}
