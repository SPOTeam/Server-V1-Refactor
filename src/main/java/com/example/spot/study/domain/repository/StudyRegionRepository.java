package com.example.spot.study.domain.repository;

import java.util.List;

import com.example.spot.study.domain.aggregate.Region;
import com.example.spot.study.domain.aggregate.StudyRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRegionRepository extends JpaRepository<StudyRegion, Long> {
    List<StudyRegion> findAllByRegion(Region region);

    void deleteByStudyId(Long studyId);
}
