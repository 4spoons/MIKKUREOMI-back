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

    // 특정 날짜(어제)에 DailyReport가 존재하는 모든 Profile ID를 조회 (월간 리포트 업데이트 대상 유저 찾는 용도)
    @Query("SELECT DISTINCT dr.profile.profileId FROM DailyReport dr WHERE dr.date = :date")
    List<Long> findProfileIdsByDate(@Param("date") LocalDate date);

    // 월별 통계 계산을 위한 메서드
    @Query("SELECT SIZE(dr.meals), dr.score " +
            "FROM DailyReport dr " +
            "WHERE dr.profile.profileId = :profileId AND YEAR(dr.date) = :year AND MONTH(dr.date) = :month")
    List<Object[]> findMonthlyStatsByProfileIdAndMonth(@Param("profileId") Long profileId, @Param("year") int year, @Param("month") int month);

    // 특정 월의 모든 DailyReport를 조회 (AI 프롬프트 생성용)
    @Query("SELECT dr FROM DailyReport dr JOIN FETCH dr.profile p " +
            "WHERE p.profileId = :profileId AND YEAR(dr.date) = :year AND MONTH(dr.date) = :month " +
            "ORDER BY dr.date ASC")
    List<DailyReport> findAllByProfileIdAndMonth(@Param("profileId") Long profileId, @Param("year") int year, @Param("month") int month);
}