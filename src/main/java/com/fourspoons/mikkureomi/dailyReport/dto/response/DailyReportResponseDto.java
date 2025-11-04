package com.fourspoons.mikkureomi.dailyReport.dto.response;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DailyReportResponseDto {

    private final Long dailyReportId;
    private final Long profileId;
    private final LocalDate date;
    private final Integer score;
    private final String comment;


    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    // 연결된 Meal 개수 (프론트엔드 목록 화면용)
    private final int mealCount;

    // Meal ID 리스트
    private final List<Long> mealIds;

    private final BigDecimal dailyCalories;
    private final BigDecimal dailyCarbohydrates;
    private final BigDecimal dailyDietaryFiber;
    private final BigDecimal dailyProtein;
    private final BigDecimal dailyFat;
    private final BigDecimal dailySugars;
    private final BigDecimal dailySodium;

    public DailyReportResponseDto(DailyReport dailyReport) {
        this.dailyReportId = dailyReport.getDailyReportId();
        this.profileId = dailyReport.getProfile().getProfileId();
        this.date = dailyReport.getDate();
        this.score = dailyReport.getScore();
        this.comment = dailyReport.getComment();
        this.createdAt = dailyReport.getCreatedAt();
        this.modifiedAt = dailyReport.getModifiedAt();

        // Meal ID 리스트 및 개수 설정
        if (dailyReport.getMeals() != null) {
            this.mealIds = dailyReport.getMeals().stream()
                    .map(meal -> meal.getMealId())
                    .collect(Collectors.toList());
            this.mealCount = this.mealIds.size();
        } else {
            this.mealIds = List.of();
            this.mealCount = 0;
        }

        // 누적 영양 성분 매핑
        this.dailyCalories = dailyReport.getDailyCalories();
        this.dailyCarbohydrates = dailyReport.getDailyCarbohydrates();
        this.dailyDietaryFiber = dailyReport.getDailyDietaryFiber();
        this.dailyProtein = dailyReport.getDailyProtein();
        this.dailyFat = dailyReport.getDailyFat();
        this.dailySugars = dailyReport.getDailySugars();
        this.dailySodium = dailyReport.getDailySodium();
    }
}