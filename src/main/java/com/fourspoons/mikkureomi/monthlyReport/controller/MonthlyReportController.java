package com.fourspoons.mikkureomi.monthlyReport.controller;

import com.fourspoons.mikkureomi.dailyReport.dto.response.DailyReportResponseDto;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.monthlyReport.dto.response.MonthlyReportResponseDto;
import com.fourspoons.mikkureomi.monthlyReport.service.MonthlyReportService;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monthly-reports")
@RequiredArgsConstructor
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;
    private final ProfileService profileService;

    // 특정 프로필의 특정 연/월 월간 보고서를 조회
    @GetMapping("/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlyReportResponseDto>> getMonthlyReport(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Integer year, @PathVariable Integer month) {

        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        MonthlyReportResponseDto reportDto = monthlyReportService.getMonthlyReport(profileId, year, month);

        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MONTHLY_REPORT_SUCCESS.getMessage(), reportDto));
    }
}