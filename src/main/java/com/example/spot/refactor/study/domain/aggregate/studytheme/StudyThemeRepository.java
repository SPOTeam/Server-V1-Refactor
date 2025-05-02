package com.example.spot.refactor.study.domain.aggregate.studytheme;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface StudyThemeRepository extends JpaRepository<StudyTheme, Long> {
    List<StudyTheme> findAllByTheme(Theme theme);
    void deleteByStudyId(Long studyId);

}