package com.fourspoons.mikkureomi.dailyReport.controller;

import com.fourspoons.mikkureomi.dailyReport.dto.response.DailyReportResponseDto;
import com.fourspoons.mikkureomi.dailyReport.service.DailyReportService;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<DailyReportResponseDto> getDailyReportByDate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("date") LocalDate date) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        DailyReportResponseDto responseDto = dailyReportService.getDailyReportByDate(profileId, date);
        return ResponseEntity.ok(responseDto);
    }

    // 2. 단일 DailyReport 삭제
    @DeleteMapping("/{dailyReportId}")
    public ResponseEntity<Void> deleteDailyReport(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long dailyReportId) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        dailyReportService.deleteDailyReport(dailyReportId, profileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}