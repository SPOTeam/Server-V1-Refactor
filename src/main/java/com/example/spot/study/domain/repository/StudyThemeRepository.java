package com.example.spot.study.domain.repository;

import java.util.List;

import com.example.spot.study.domain.association.StudyTheme;
import com.example.spot.study.domain.association.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface StudyThemeRepository extends JpaRepository<StudyTheme, Long> {
    List<StudyTheme> findAllByTheme(Theme theme);
    void deleteByStudyId(Long studyId);

}