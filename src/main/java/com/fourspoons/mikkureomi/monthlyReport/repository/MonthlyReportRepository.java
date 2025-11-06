package com.fourspoons.mikkureomi.monthlyReport.repository;

import com.fourspoons.mikkureomi.monthlyReport.domain.MonthlyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MonthlyReportRepository extends JpaRepository<MonthlyReport, Long> {

    // year와 month를 기준으로 조회하도록 변경
    Optional<MonthlyReport> findByProfile_ProfileIdAndYearAndMonth(Long profileId, Integer year, Integer month);
}