package com.example.spot.study.domain.repository;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.study.domain.association.Theme;
import com.example.spot.study.domain.enums.ThemeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

    Optional<Theme> findByThemeType(ThemeType themeType);

    boolean existsByThemeType(ThemeType themeType);

    default Theme getByThemeType(ThemeType themeType) {
        return findByThemeType(themeType)
                .orElseThrow(() -> new GeneralException(ErrorStatus._THEME_NOT_FOUND));
    }

}
