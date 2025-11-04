package com.fourspoons.mikkureomi.dailyReport.controller;

import com.fourspoons.mikkureomi.dailyReport.dto.response.DailyReportResponseDto;
import com.fourspoons.mikkureomi.dailyReport.service.DailyReportService;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/daily-reports")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService dailyReportService;
    private final ProfileService profileService;

    // 1. 단일 DailyReport 조회
    @GetMapping
    public ResponseEntity<ApiResponse<DailyReportResponseDto>> getDailyReportByDate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("date") LocalDate date) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        DailyReportResponseDto responseDto = dailyReportService.getDailyReportByDate(profileId, date);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_DAILY_REPORT_SUCCESS.getMessage(), responseDto));
    }

    // 2. 단일 DailyReport 삭제
//    @DeleteMapping("/{dailyReportId}")
//    public ResponseEntity<ApiResponse<Void>> deleteDailyReport(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long dailyReportId) {
//        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
//        dailyReportService.deleteDailyReport(dailyReportId, profileId);
//        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.DELETE_DAILY_REPORT_SUCCESS.getMessage()));
//    }
}