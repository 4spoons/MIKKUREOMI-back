package com.fourspoons.mikkureomi.monthlyReport.dto.response;

import com.fourspoons.mikkureomi.monthlyReport.domain.MonthlyReport;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlyReportResponseDto {

    private Long monthlyReportId;
    private Integer score;
    private String comment;
    private Integer year;
    private Integer month;
    private Integer totalMeals;
    private Integer totalDays;
    private Integer missingDays;

    // MonthlyReport 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static MonthlyReportResponseDto from(MonthlyReport report) {
        return MonthlyReportResponseDto.builder()
                .monthlyReportId(report.getMonthlyReportId())
                .score(report.getScore())
                .comment(report.getComment())
                .year(report.getYear())
                .month(report.getMonth())
                .totalMeals(report.getTotalMeals())
                .totalDays(report.getTotalDays())
                .missingDays(report.getMissingDays())
                .build();
    }
}