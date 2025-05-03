package com.example.spot.refactor.study.domain.aggregate;

import com.example.spot.refactor.study.domain.enums.ThemeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    Optional<Theme> findByThemeType(ThemeType themeType);
    boolean existsByThemeType(ThemeType themeType);

}
