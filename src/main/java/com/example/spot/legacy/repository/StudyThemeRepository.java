package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.Theme;
import com.example.spot.legacy.domain.mapping.StudyTheme;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface StudyThemeRepository extends JpaRepository<StudyTheme, Long> {
    List<StudyTheme> findAllByTheme(Theme theme);
    StudyTheme findByTheme(Theme theme);

    void deleteByStudyId(Long studyId);

}