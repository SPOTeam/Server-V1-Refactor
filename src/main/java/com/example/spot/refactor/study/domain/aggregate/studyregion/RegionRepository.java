package com.example.spot.refactor.study.domain.aggregate.studyregion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    Optional<Region> findByCode(String code);
    boolean existsByCode(String code);
}
