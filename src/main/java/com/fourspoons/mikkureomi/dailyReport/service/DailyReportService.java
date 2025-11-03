package com.fourspoons.mikkureomi.dailyReport.service;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.dailyReport.dto.response.DailyReportResponseDto;
import com.fourspoons.mikkureomi.dailyReport.repository.DailyReportRepository;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final ProfileRepository profileRepository;

    /** * 1. 단일 DailyReport 조회 (Read One by Date and Profile)
     */
    public DailyReportResponseDto getDailyReportByDate(Long profileId, LocalDate date) {
        DailyReport report = dailyReportRepository.findByProfile_ProfileIdAndDate(profileId, date)
                .orElseThrow(() -> new CustomException(ErrorMessage.DAILY_REPORT_NOT_FOUND));

        // DTO 변환 시 연결된 Meal 목록도 함께 로딩됨
        return new DailyReportResponseDto(report);
    }

    // 2. 단일 DailyReport 삭제 (Delete One)
    @Transactional
    public void deleteDailyReport(Long dailyReportId, Long profileId) {
        DailyReport report = dailyReportRepository.findById(dailyReportId)
                .orElseThrow(() -> new CustomException(ErrorMessage.DAILY_REPORT_NOT_FOUND));

        // 작성자 ID와 현재 사용자 ID 비교
        Long reportOwnerProfileId = report.getProfile().getProfileId();

        // ID가 다르면 권한 없음 예외 발생
        if (!reportOwnerProfileId.equals(profileId)) {
            throw new CustomException(ErrorMessage.ACCESS_DENIED);
        }

        dailyReportRepository.delete(report);
    }

    // MealService에서 호출할 DailyReport 조회/생성 로직
    @Transactional
    public DailyReport getOrCreateDailyReport(Long profileId, LocalDate date) {
        return dailyReportRepository.findByProfile_ProfileIdAndDate(profileId, date)
                .orElseGet(() -> createNewDailyReport(profileId, date));
    }

    // DailyReport 생성 로직
    private DailyReport createNewDailyReport(Long profileId, LocalDate date) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PROFILE_NOT_FOUND));

        DailyReport newReport = DailyReport.builder()
                .profile(profile)
                .date(date)
                .score(0) // 초기 점수 0점
                .comment("오늘의 첫 식사입니다.") // 초기 코멘트
                .build();

        return dailyReportRepository.save(newReport);
    }

    // DailyReport 점수/코멘트 업데이트 로직 (Meal이 추가될 때마다 호출)
    @Transactional
    public void updateReportOnNewMeal(DailyReport report) {

        Integer newScore = Math.min(100, report.getScore() + 10); // 점수 10점씩 증가 (최대 100점)
        String newComment = String.format("총 XX개의 식사가 기록되었습니다. 잘하고 있어요!");

        report.updateReport(newScore, newComment);
    }
}