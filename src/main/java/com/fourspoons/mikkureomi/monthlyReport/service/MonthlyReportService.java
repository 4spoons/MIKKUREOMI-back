package com.fourspoons.mikkureomi.monthlyReport.service;

import com.fourspoons.mikkureomi.dailyReport.repository.DailyReportRepository;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.monthlyReport.domain.MonthlyReport;
import com.fourspoons.mikkureomi.monthlyReport.repository.MonthlyReportRepository;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyReportRepository;
    private final DailyReportRepository dailyReportRepository; // 월간 통계 계산을 위해 필요
    private final ProfileRepository profileRepository;

    // --- 1. 생성 로직 (해당 월의 첫 DailyReport가 생성될 때 MonthlyReport를 생성) ---
    @Transactional
    public MonthlyReport getOrCreateMonthlyReport(Profile profile, LocalDate date) {

        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        // year와 month를 사용하여 조회
        return monthlyReportRepository.findByProfile_ProfileIdAndYearAndMonth(profile.getProfileId(), year, month)
                .orElseGet(() -> createNewMonthlyReport(profile, year, month, date));
    }

    private MonthlyReport createNewMonthlyReport(Profile profile, Integer year, Integer month, LocalDate date) {

        MonthlyReport newReport = MonthlyReport.builder()
                .profile(profile)
                .year(year)
                .month(month)
                .score(0)
                .totalMeals(0)
                .totalDays(0)
                .missingDays(date.getDayOfMonth() - 1)
                .comment("")
                .build();

        return monthlyReportRepository.save(newReport);
    }

    // --- 2. 업데이트 로직 (특정 유저의 월간 리포트를 전날 기준으로 업데이트) ---
    @Transactional
    public void updateMonthlyReport(Long profileId, LocalDate date) {

        // 1. 업데이트 대상 월 (현재 날짜의 월)
        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        // 2. 해당 월의 MonthlyReport 조회
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PROFILE_NOT_FOUND));
        MonthlyReport report = monthlyReportRepository.findByProfile_ProfileIdAndYearAndMonth(profileId, year, month)
                .orElse(null); // Optional이 비어있으면 null 반환

        // MonthlyReport가 없으면 바로 종료 (해당 월의 첫 DailyReport가 아직 생성되지 않은 경우)
        if (report == null) {
            return;
        }
        // 3. 해당 월의 DailyReport 통계 데이터 조회 (월초부터 어제까지)
        List<Object[]> dailyStats = dailyReportRepository.findMonthlyStatsByProfileIdAndMonth(profileId, year, month);

        int totalMeals = dailyStats.stream().mapToInt(row -> (Integer) row[0]).sum();
        int totalDays = dailyStats.size();

        // 4. missingDays 계산
        int daysInMonthUntilYesterday = date.getDayOfMonth() - 1; // 어제까지의 일수
        int missingDays = daysInMonthUntilYesterday - totalDays;

        // 5. score 계산 (예: DailyReport 점수의 단순 평균)
        double averageScore = dailyStats.stream().mapToDouble(row -> (Integer) row[1]).average().orElse(0.0);
        int finalScore = (int) Math.round(averageScore);


        // 6. 업데이트
        report.updateReport(finalScore, totalMeals, totalDays, missingDays);
    }
}