package com.example.spot.refactor.study.domain.aggregate.studyregion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRegionRepository extends JpaRepository<StudyRegion, Long> {
    List<StudyRegion> findAllByRegion(Region region);

    void deleteByStudyId(Long studyId);
}
