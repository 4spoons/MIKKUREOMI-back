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
import java.util.List;

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

    // 2. 특정 월에 DailyReport가 존재하는 날짜 리스트 조회 */
    @GetMapping("/dates")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getReportDatesByYearAndMonth(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("year") int year, @RequestParam("month") int month) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        List<LocalDate> dates = dailyReportService.getReportDatesByMonth(profileId, year, month);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_DAILY_REPORT_SUCCESS.getMessage(), dates));
    }

    // 3. 단일 DailyReport 삭제
//    @DeleteMapping("/{dailyReportId}")
//    public ResponseEntity<ApiResponse<Void>> deleteDailyReport(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long dailyReportId) {
//        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
//        dailyReportService.deleteDailyReport(dailyReportId, profileId);
//        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.DELETE_DAILY_REPORT_SUCCESS.getMessage()));
//    }
}