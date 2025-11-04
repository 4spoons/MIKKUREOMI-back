package com.fourspoons.mikkureomi.meal.domain;


import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import com.fourspoons.mikkureomi.mealPicture.domain.MealPicture;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Meal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mealId", nullable = false)
    private Long mealId;

    // dailyReport 연결 필요

    // MealFood와의 일대다 관계
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealFood> mealFoods = new ArrayList<>();

    // MealPicture와의 일대일 관계 (mappedBy로 주인이 아님을 명시)
    @OneToOne(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MealPicture mealPicture;

    // DailyReport와의 N:1 관계 (Meal이 N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dailyReportId") // dailyReportId 컬럼
    private DailyReport dailyReport;

//    // Setter 지양: 연관관계 설정을 위한 protected 메서드 추가 (MealPictureService에서 사용)
//    protected void setDailyReport(DailyReport dailyReport) {
//        this.dailyReport = dailyReport;
//    }

}