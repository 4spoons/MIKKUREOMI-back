package com.fourspoons.mikkureomi.dailyReport.service;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.dailyReport.dto.response.DailyReportResponseDto;
import com.fourspoons.mikkureomi.dailyReport.repository.DailyReportRepository;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealNutrientSummary;
import com.fourspoons.mikkureomi.monthlyReport.service.MonthlyReportService;
import com.fourspoons.mikkureomi.notification.service.NotificationService;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.recommendedNutrients.dto.RecommendedNutrientsResponseDto;
import com.fourspoons.mikkureomi.recommendedNutrients.service.RecommendedNutrientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fourspoons.mikkureomi.notification.domain.NotificationCategory;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final ProfileRepository profileRepository;
    private final MonthlyReportService monthlyReportService;
    private final NotificationService notificationService;
    private final NutritionScoreCalculator nutritionScoreCalculator;
    private final RecommendedNutrientsService recommendedNutrientsService;

    // 1. 단일 DailyReport 조회 (Read One by Date and Profile
    public DailyReportResponseDto getDailyReportByDate(Long profileId, LocalDate date) {
        DailyReport report = dailyReportRepository.findByProfile_ProfileIdAndDate(profileId, date)
                .orElseThrow(() -> new CustomException(ErrorMessage.DAILY_REPORT_NOT_FOUND));

        // DTO 변환 시 연결된 Meal 목록도 함께 로딩됨
        return new DailyReportResponseDto(report);
    }

    // MealService에서 호출할 DailyReport 조회/생성 로직
    @Transactional
    public DailyReport getOrCreateDailyReport(Long profileId, LocalDate date) {
        return dailyReportRepository.findByProfile_ProfileIdAndDate(profileId, date)
                .orElseGet(() -> createNewDailyReport(profileId, date));
    }

    // DailyReport 생성 로직
    private DailyReport createNewDailyReport(Long profileId, LocalDate date) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PROFILE_NOT_FOUND));

        // 1. MonthlyReport 생성/조회 (DailyReport 생성 시점에 해당 월의 MonthlyReport 생성)
        monthlyReportService.getOrCreateMonthlyReport(profile, date);

        // 2. DailyReport 생성 및 저장
        DailyReport newReport = DailyReport.builder()
                .profile(profile)
                .date(date)
                .score(0) // 초기 점수 0점
                .build();

        return dailyReportRepository.save(newReport);
    }

    // DailyReport 점수 업데이트 로직 (Meal이 추가될 때마다 호출)
    @Transactional
    public void updateReportOnNewMeal(Long profileId, DailyReport report) {

        RecommendedNutrientsResponseDto recommendedNutrients = recommendedNutrientsService.getRecommendedNutrients(profileId);

        // 점수 계산
        Integer newScore = nutritionScoreCalculator.calculateScore(report, recommendedNutrients);

        report.updateReport(newScore);

        final int TARGET_SCORE = 70;

        // 70점 달성시 알림 전송
        if (report.getScore() >= TARGET_SCORE) {

            notificationService.createNotificationLog(
                    profileId,
                    NotificationCategory.GOOD_SCORE_ALERT,
                    NotificationCategory.GOOD_SCORE_ALERT.getDefaultContent(), // "일일 식사 영양 점수 70점 이상!..."
                    "mikkureomi://app/report/daily" // 일일 보고서 페이지 Deep Link
            );
            System.out.println("LOGGED: Profile " + profileId + " 70점 달성 알림 기록.");
        }
    }

    @Transactional
    public void accumulateNutrients(DailyReport report, MealNutrientSummary summary) {
        report.addNutrients(summary);
        // 변경 감지로 저장됩니다.
    }

    // 특정 월에 DailyReport가 존재하는 모든 날짜를 반환
    public List<LocalDate> getReportDatesByMonth(Long profileId, int year, int month) {

        if (month < 1 || month > 12) {
            // 월(month) 값이 유효하지 않을 경우 예외 처리
            throw new CustomException(ErrorMessage.INVALID_INPUT_VALUE);
        }

        return dailyReportRepository.findReportDatesByProfileIdAndYearAndMonth(
                profileId, year, month);
    }
}