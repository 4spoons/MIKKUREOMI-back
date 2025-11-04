package com.fourspoons.mikkureomi.dailyReport.repository;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
    // 특정 프로필의 특정 날짜에 해당하는 DailyReport를 조회
    Optional<DailyReport> findByProfile_ProfileIdAndDate(Long profileId, LocalDate date);

    // 특정 프로필의 특정 연/월에 해당하는 DailyReport의 날짜(date)만 조회
    @Query("SELECT dr.date FROM DailyReport dr " +
            "WHERE dr.profile.profileId = :profileId " +
            "AND YEAR(dr.date) = :year " +
            "AND MONTH(dr.date) = :month " +
            "ORDER BY dr.date ASC")
    List<LocalDate> findReportDatesByProfileIdAndYearAndMonth(
            @Param("profileId") Long profileId,
            @Param("year") int year,
            @Param("month") int month);
}