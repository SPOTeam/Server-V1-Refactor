package com.example.spot.legacy.repository;

import com.example.spot.refactor.report.domain.MemberReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Long> {
}
