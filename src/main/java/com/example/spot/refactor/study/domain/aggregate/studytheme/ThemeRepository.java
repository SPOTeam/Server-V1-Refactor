package com.example.spot.refactor.study.domain.aggregate.studytheme;

import com.example.spot.legacy.domain.enums.ThemeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    Optional<Theme> findByStudyTheme(ThemeType themeType);
    boolean existsByStudyTheme(ThemeType themeType);

}
