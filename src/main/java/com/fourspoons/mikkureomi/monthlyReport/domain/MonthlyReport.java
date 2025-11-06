package com.fourspoons.mikkureomi.monthlyReport.domain;

import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monthly_report")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthlyReport extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_report_id", nullable = false)
    private Long monthlyReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profileId", nullable = false)
    private Profile profile;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "report_year", nullable = false)
    private Integer year;

    @Column(name = "report_month", nullable = false)
    private Integer month;

    @Column(name = "total_meals", nullable = false)
    private Integer totalMeals;

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;

    @Column(name = "missing_days", nullable = false)
    private Integer missingDays;

    // 월간 리포트 정보 업데이트
    public void updateReport(Integer score, Integer totalMeals, Integer totalDays, Integer missingDays, String comment) {
        this.score = score;
        this.totalMeals = totalMeals;
        this.totalDays = totalDays;
        this.missingDays = missingDays;
        this.comment = comment;
    }
}