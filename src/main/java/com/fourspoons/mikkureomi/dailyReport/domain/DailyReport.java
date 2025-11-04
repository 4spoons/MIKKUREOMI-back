package com.fourspoons.mikkureomi.dailyReport.domain;


import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealNutrientSummary;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_report")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DailyReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_report_id", nullable = false)
    private Long dailyReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile; // 1:N 관계 (N이 DailyReport)

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "monthlyReportId") // 월간 리포트는 나중에 구현
    // private MonthlyReport monthlyReport;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "score", nullable = false)
    private Integer score; // 0 ~ 100

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "daily_calories")
    @Builder.Default
    private BigDecimal dailyCalories = BigDecimal.ZERO;

    @Column(name = "daily_carbohydrates")
    @Builder.Default
    private BigDecimal dailyCarbohydrates = BigDecimal.ZERO;

    @Column(name = "daily_dietary_fiber")
    @Builder.Default
    private BigDecimal dailyDietaryFiber = BigDecimal.ZERO;

    @Column(name = "daily_protein")
    @Builder.Default
    private BigDecimal dailyProtein = BigDecimal.ZERO;

    @Column(name = "daily_fat")
    @Builder.Default
    private BigDecimal dailyFat = BigDecimal.ZERO;

    @Column(name = "daily_sugars")
    @Builder.Default
    private BigDecimal dailySugars = BigDecimal.ZERO;

    @Column(name = "daily_sodium")
    @Builder.Default
    private BigDecimal dailySodium = BigDecimal.ZERO;


    // DailyReport <-> Meal (1:N 관계, DailyReport가 1)
    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();

    public void updateReport(Integer newScore, String newComment) {
        this.score = newScore;
        this.comment = newComment;
    }

    public void addNutrients(MealNutrientSummary summary) {
        this.dailyCalories = this.dailyCalories.add(summary.getTotalCalories());
        this.dailyCarbohydrates = this.dailyCarbohydrates.add(summary.getTotalCarbohydrates());
        this.dailyDietaryFiber = this.dailyDietaryFiber.add(summary.getTotalDietaryFiber());
        this.dailyProtein = this.dailyProtein.add(summary.getTotalProtein());
        this.dailyFat = this.dailyFat.add(summary.getTotalFat());
        this.dailySugars = this.dailySugars.add(summary.getTotalSugars());
        this.dailySodium = this.dailySodium.add(summary.getTotalSodium());
    }

}