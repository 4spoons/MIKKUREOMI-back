package com.fourspoons.mikkureomi.dailyReport.dto.response;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import lombok.Getter;

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

    // 연결된 Meal 목록 (상세 조회용, 필요한 경우만 채워서 사용)
    private final List<MealResponseDto> meals;

    public DailyReportResponseDto(DailyReport dailyReport) {
        this.dailyReportId = dailyReport.getDailyReportId();
        this.profileId = dailyReport.getProfile().getProfileId();
        this.date = dailyReport.getDate();
        this.score = dailyReport.getScore();
        this.comment = dailyReport.getComment();
        this.createdAt = dailyReport.getCreatedAt();
        this.modifiedAt = dailyReport.getModifiedAt();

        // Meal 목록과 개수 설정
        if (dailyReport.getMeals() != null) {
            this.meals = dailyReport.getMeals().stream()
                    .map(MealResponseDto::new)
                    .collect(Collectors.toList());
            this.mealCount = this.meals.size();
        } else {
            this.meals = List.of();
            this.mealCount = 0;
        }
    }
}