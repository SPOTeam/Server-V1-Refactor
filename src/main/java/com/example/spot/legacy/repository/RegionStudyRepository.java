package com.example.spot.legacy.repository;

import com.example.spot.legacy.domain.Region;
import com.example.spot.legacy.domain.mapping.RegionStudy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionStudyRepository extends JpaRepository<RegionStudy, Long> {
    List<RegionStudy> findAllByRegion(Region region);

    void deleteByStudyId(Long studyId);
}
