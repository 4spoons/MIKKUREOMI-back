package com.fourspoons.mikkureomi.monthlyReport.service;

import com.fourspoons.mikkureomi.dailyReport.repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MonthlyReportScheduler {

    private final MonthlyReportService monthlyReportService;
    private final DailyReportRepository dailyReportRepository;

    // 매일 자정(00:00)에 실행되어 어제 기록을 기반으로 MonthlyReport를 업데이트
    @Scheduled(cron = "0 0 0 * * *") // 매일 00시 00분 00초에 실행
    public void updateMonthlyReportsForAllUsers() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. 어제 DailyReport를 기록한 모든 Profile ID를 조회 (DailyReportRepository에 필요)
        List<Long> profileIds = dailyReportRepository.findProfileIdsByDate(yesterday);

        if (profileIds.isEmpty()) {
            return;
        }

        // 2. 해당 유저들의 MonthlyReport를 업데이트
        for (Long profileId : profileIds) {
            monthlyReportService.updateMonthlyReport(profileId, LocalDate.now()); // 오늘 날짜를 기준으로 월을 계산
        }
    }
}