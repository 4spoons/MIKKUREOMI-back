package com.fourspoons.mikkureomi.monthlyReport.service;

import com.fourspoons.mikkureomi.chatgptAnalysis.dto.response.ChatGPTResponseDto;
import com.fourspoons.mikkureomi.chatgptAnalysis.service.ChatGPTService;
import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.dailyReport.repository.DailyReportRepository;
import com.fourspoons.mikkureomi.exception.CustomException;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.monthlyReport.domain.MonthlyReport;
import com.fourspoons.mikkureomi.monthlyReport.repository.MonthlyReportRepository;
import com.fourspoons.mikkureomi.profile.domain.Profile;
import com.fourspoons.mikkureomi.profile.repository.ProfileRepository;
import com.fourspoons.mikkureomi.recommendedNutrients.dto.RecommendedNutrientsResponseDto;
import com.fourspoons.mikkureomi.recommendedNutrients.service.RecommendedNutrientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyReportService {

    private final MonthlyReportRepository monthlyReportRepository;
    private final DailyReportRepository dailyReportRepository; // 월간 통계 계산을 위해 필요
    private final ChatGPTService chatGPTService;
    private final ProfileRepository profileRepository;
    private final RecommendedNutrientsService recommendedNutrientsService;

    // --- 1. 생성 로직 (해당 월의 첫 DailyReport가 생성될 때 MonthlyReport를 생성) ---
    @Transactional
    public MonthlyReport getOrCreateMonthlyReport(Profile profile, LocalDate date) {

        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        // year와 month를 사용하여 조회
        return monthlyReportRepository.findByProfile_ProfileIdAndYearAndMonth(profile.getProfileId(), year, month)
                .orElseGet(() -> createNewMonthlyReport(profile, year, month, date));
    }

    private MonthlyReport createNewMonthlyReport(Profile profile, Integer year, Integer month, LocalDate date) {

        MonthlyReport newReport = MonthlyReport.builder()
                .profile(profile)
                .year(year)
                .month(month)
                .score(0)
                .totalMeals(0)
                .totalDays(0)
                .missingDays(date.getDayOfMonth() - 1)
                .comment("")
                .build();

        return monthlyReportRepository.save(newReport);
    }

    // --- 2. 업데이트 로직 (특정 유저의 월간 리포트를 전날 기준으로 업데이트) ---
    @Transactional
    public void updateMonthlyReport(Long profileId, LocalDate date) {

        // 1. 업데이트 대상 월 (현재 날짜의 월)
        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        // 2. 해당 월의 MonthlyReport 조회
        MonthlyReport report = monthlyReportRepository.findByProfile_ProfileIdAndYearAndMonth(profileId, year, month)
                .orElse(null); // Optional이 비어있으면 null 반환

        // MonthlyReport가 없으면 바로 종료 (해당 월의 첫 DailyReport가 아직 생성되지 않은 경우)
        if (report == null) {
            return;
        }
        // 3. 해당 월의 DailyReport 통계 데이터 조회 (월초부터 어제까지)
        List<Object[]> dailyStats = dailyReportRepository.findMonthlyStatsByProfileIdAndMonth(profileId, year, month);

        int totalMeals = dailyStats.stream().mapToInt(row -> (Integer) row[0]).sum();
        int totalDays = dailyStats.size();

        // 4. missingDays 계산
        int daysInMonthUntilYesterday = date.getDayOfMonth() - 1; // 어제까지의 일수
        int missingDays = daysInMonthUntilYesterday - totalDays;

        // 5. score 계산 (예: DailyReport 점수의 단순 평균)
        double averageScore = dailyStats.stream().mapToDouble(row -> (Integer) row[1]).average().orElse(0.0);
        int finalScore = (int) Math.round(averageScore);

        String requestText = buildAiPromptString(profileId, year, month, totalMeals, totalDays, missingDays);

        ChatGPTResponseDto response = chatGPTService.requestTextAnalysis(requestText);
        String comment = response.getChoices().get(0).getMessage().getContent();

        // 6. 업데이트
        report.updateReport(finalScore, totalMeals, totalDays, missingDays, comment);
    }


    private String buildAiPromptString(Long profileId, Integer year, Integer month,
                                       int totalMeals, int totalDays, int missingDays) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new CustomException(ErrorMessage.PROFILE_NOT_FOUND));

        RecommendedNutrientsResponseDto recommendedNutrients = recommendedNutrientsService.getRecommendedNutrients(profileId);

        List<DailyReport> reports = dailyReportRepository.findAllByProfileIdAndMonth(profileId, year, month);

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("날짜,칼로리,탄수화물,단백질,지방,당류,나트륨,식이섬유,점수\n");

        for (DailyReport dr : reports) {
            // BigDecimal 값을 문자열로 변환하여 CSV에 추가
            csvBuilder.append(dr.getDate().toString()).append(",");
            csvBuilder.append(dr.getDailyCalories().toPlainString()).append(",");
            csvBuilder.append(dr.getDailyCarbohydrates().toPlainString()).append(",");
            csvBuilder.append(dr.getDailyProtein().toPlainString()).append(",");
            csvBuilder.append(dr.getDailyFat().toPlainString()).append(",");
            csvBuilder.append(dr.getDailySugars().toPlainString()).append(",");
            csvBuilder.append(dr.getDailySodium().toPlainString()).append(",");
            csvBuilder.append(dr.getDailyDietaryFiber().toPlainString()).append(",");
            csvBuilder.append(dr.getScore()).append("\n");
        }

        String promptTemplate = String.format(
                """
                너는 사용자의 건강한 식습관 여정을 돕는 친절하고 상세한 개인 영양 코치 AI야. 사용자가 좌절하지 않도록 긍정적이고 격려하는 어투를 사용하고, 실용적인 개선 방안을 제시해야 해.
                너는 사용자 경험 개선을 위해 **'왜 이런 결과가 나왔는지'를 명확히 설명하는 설명 가능한 AI (XAI) 원칙**을 반드시 준수해야 한다.
                분석 시, [권장량 정보]와 [날짜별 상세 DailyReport 기록]을 비교하여 피드백을 생성해야 한다.
                
                [출력 형식]
                반드시 아래 JSON 형식만을 따르며, 다른 서론이나 설명은 일체 포함하지 않도록 해.
                {
                    "feedbacks": [
                        {
                            "main_message": "메인 피드백 (공백 포함 약 100자의 영양학적 조언)",
                            "evidence_message": "피드백에 대한 수치적 근거/기준 제시 (공백 포함 약 100자의 정확한 데이터 기반)"
                        },
                        { ... 두 번째 피드백 ... },
                        { ... 세 번째 피드백 ... }
                    ]
                }
                
                [분석 데이터]
                1. 사용자 프로필: %s (%d세)
                2. 월간 기록 요약: 총 기록 식사 횟수: %d회, 기록 일수: %d일, 누락 일수: %d일
                
                3. 일일 영양소 권장량:
                   - 에너지(kcal): %d
                   - 탄수화물(g): %.2f
                   - 단백질(g): %.2f
                   - 지방(g): %.2f
                   - 나트륨(mg): %d
                   - 당(g): %.2f
                   - 식이섬유(g): %.2f
    
                4. 날짜별 상세 DailyReport 기록 (CSV):
                %s
                
                [피드백 요구사항]
                - 총 3쌍의 메인 메시지와 증명 메시지를 생성해야 한다.
                - 메인 메시지는 공백 포함 약 100자로 작성하며, 개선할 영양소1, 개선할 영양소1, 생활 식습관(긍정적 습관(예: 높은 점수), 기록 습관 등) 중 3가지 주제를 다룬다. 단순 분석 결과를 넘어 긍정적인 측면 강화와 구체적인 개선 행동 제안을 필수로 포함한다.
                - 증명 메시지는 메인 메시지의 근거가 되는 평균, 기준치, 비율 등의 수치적이고 정확한 근거를 포함하며, 공백 포함 100자 내외로 작성한다.
                - 한국어로 답변해야 합니다.
                """
                , profile.getGender().name(), LocalDate.now().getYear() - profile.getBirthYear(), totalMeals, totalDays, missingDays,
                recommendedNutrients.getCalories(), recommendedNutrients.getCarbohydrates(), recommendedNutrients.getProtein(), recommendedNutrients.getFat(), recommendedNutrients.getSodium(), recommendedNutrients.getSugars(), recommendedNutrients.getDietaryFiber(),
                csvBuilder.toString()
        );

        return promptTemplate;
    }
}