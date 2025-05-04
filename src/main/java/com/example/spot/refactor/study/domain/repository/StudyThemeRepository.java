package com.example.spot.refactor.study.domain.repository;

import java.util.List;

import com.example.spot.refactor.study.domain.aggregate.StudyTheme;
import com.example.spot.refactor.study.domain.aggregate.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface StudyThemeRepository extends JpaRepository<StudyTheme, Long> {
    List<StudyTheme> findAllByTheme(Theme theme);
    void deleteByStudyId(Long studyId);

}