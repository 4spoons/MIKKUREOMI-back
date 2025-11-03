package com.fourspoons.mikkureomi.dailyReport.repository;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    // 특정 프로필의 특정 날짜에 해당하는 DailyReport를 조회
    Optional<DailyReport> findByProfile_ProfileIdAndDate(Long profileId, LocalDate date);
}