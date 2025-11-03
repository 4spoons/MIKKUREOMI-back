package com.fourspoons.mikkureomi.dailyReport.domain;


import com.fourspoons.mikkureomi.common.BaseTimeEntity;
import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "date", nullable = false, unique = true) // 하루에 하나만 가능 (실제 DB에서는 profileId와 복합 유니크 키로 설정 권장)
    private LocalDate date;

    @Column(name = "score", nullable = false)
    private Integer score; // 0 ~ 100

    @Column(name = "comment", length = 500)
    private String comment;

    // DailyReport <-> Meal (1:N 관계, DailyReport가 1)
    @OneToMany(mappedBy = "dailyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Meal> meals = new ArrayList<>();

    public void updateReport(Integer newScore, String newComment) {
        this.score = newScore;
        this.comment = newComment;
    }

}