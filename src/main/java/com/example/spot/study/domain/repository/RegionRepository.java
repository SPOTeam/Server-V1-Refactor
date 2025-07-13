package com.example.spot.study.domain.repository;

import com.example.spot.common.api.code.status.ErrorStatus;
import com.example.spot.common.api.exception.GeneralException;
import com.example.spot.study.domain.association.Region;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);

    boolean existsByCode(String code);

    default Region getByCode(String code) {
        return findByCode(code)
                .orElseThrow(() -> new GeneralException(ErrorStatus._REGION_NOT_FOUND));
    }
}
