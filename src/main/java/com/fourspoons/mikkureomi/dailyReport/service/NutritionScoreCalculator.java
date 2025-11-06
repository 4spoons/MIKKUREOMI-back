package com.fourspoons.mikkureomi.dailyReport.service;

import com.fourspoons.mikkureomi.dailyReport.domain.DailyReport;
import com.fourspoons.mikkureomi.recommendedNutrients.dto.RecommendedNutrientsResponseDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Component
public class NutritionScoreCalculator {

    // 계산 정확도를 위한 상수
    private static final int SCALE = 10;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);
    private static final BigDecimal NINE = BigDecimal.valueOf(9);
    private static final BigDecimal SEVEN = BigDecimal.valueOf(7);

    // 가중치 설정 (BigDecimal로 변환하여 사용)
    private static final Map<String, BigDecimal> WEIGHTS = Map.of(
            "CALORIES", BigDecimal.valueOf(20.0), "PROTEIN", BigDecimal.valueOf(20.0), "SODIUM", BigDecimal.valueOf(15.0),
            "CARBS", BigDecimal.valueOf(15.0), "FAT", BigDecimal.valueOf(10.0), "FIBER", BigDecimal.valueOf(10.0), "SUGAR", BigDecimal.valueOf(10.0)
    );
    private static final BigDecimal TOTAL_WEIGHT = BigDecimal.valueOf(100.0);

    // 감점 계수 (BigDecimal)
    private static final BigDecimal OVER_RANGE_PENALTY_FACTOR = BigDecimal.valueOf(200.0);
    private static final BigDecimal LIMIT_EXCEED_PENALTY_FACTOR = BigDecimal.valueOf(300.0);


    // 7가지 영양소의 달성도를 기반으로 최종 점수(0-100)를 계산
    public Integer calculateScore(DailyReport report, RecommendedNutrientsResponseDto target) {

        // DailyReport에서 BigDecimal 타입으로 섭취량 가져오기
        BigDecimal actualCal = report.getDailyCalories();
        BigDecimal actualCarbs = report.getDailyCarbohydrates();
        BigDecimal actualProtein = report.getDailyProtein();
        BigDecimal actualFat = report.getDailyFat();
        BigDecimal actualFiber = report.getDailyDietaryFiber();
        BigDecimal actualSodium = report.getDailySodium();
        BigDecimal actualSugar = report.getDailySugars();

        // RecommendedNutrients에서 Integer/Double을 BigDecimal로 변환
        BigDecimal targetEnergy = BigDecimal.valueOf(target.getCalories());
        BigDecimal targetFiber = BigDecimal.valueOf(target.getDietaryFiber());
        BigDecimal targetSodium = BigDecimal.valueOf(target.getSodium());
        BigDecimal targetSugars = BigDecimal.valueOf(target.getSugars());


        // 1. 적정 비율(EER) 기반 목표량 (g) 계산 (KDRIs 일반 비율 적용)
        // [수식 예시: (Target Energy * EER 비율) / 에너지 변환 계수]

        // 단백질 (7% ~ 20%)
        BigDecimal proteinMinG = targetEnergy.multiply(BigDecimal.valueOf(0.07)).divide(FOUR, SCALE, ROUNDING_MODE);
        BigDecimal proteinMaxG = targetEnergy.multiply(BigDecimal.valueOf(0.20)).divide(FOUR, SCALE, ROUNDING_MODE);

        // 탄수화물 (55% ~ 65%)
        BigDecimal carbsMinG = targetEnergy.multiply(BigDecimal.valueOf(0.55)).divide(FOUR, SCALE, ROUNDING_MODE);
        BigDecimal carbsMaxG = targetEnergy.multiply(BigDecimal.valueOf(0.65)).divide(FOUR, SCALE, ROUNDING_MODE);

        // 지방 (15% ~ 30%)
        BigDecimal fatMinG = targetEnergy.multiply(BigDecimal.valueOf(0.15)).divide(NINE, SCALE, ROUNDING_MODE);
        BigDecimal fatMaxG = targetEnergy.multiply(BigDecimal.valueOf(0.30)).divide(NINE, SCALE, ROUNDING_MODE);


        BigDecimal weightedScoreSum = BigDecimal.ZERO;

        // 2. 점수 계산 및 가중치 적용

        // 2-1. 범위 목표 (±10% 허용 범위)
        weightedScoreSum = weightedScoreSum.add(
                calculateRangeScore(actualCal, targetEnergy.multiply(BigDecimal.valueOf(0.9)), targetEnergy.multiply(BigDecimal.valueOf(1.1)))
                        .multiply(WEIGHTS.get("CALORIES"))
        );
        weightedScoreSum = weightedScoreSum.add(
                calculateRangeScore(actualFiber, targetFiber.multiply(BigDecimal.valueOf(0.9)), targetFiber.multiply(BigDecimal.valueOf(1.1)))
                        .multiply(WEIGHTS.get("FIBER"))
        );

        // 2-2. EER 기반 다량 영양소
        weightedScoreSum = weightedScoreSum.add(calculateRangeScore(actualProtein, proteinMinG, proteinMaxG).multiply(WEIGHTS.get("PROTEIN")));
        weightedScoreSum = weightedScoreSum.add(calculateRangeScore(actualCarbs, carbsMinG, carbsMaxG).multiply(WEIGHTS.get("CARBS")));
        weightedScoreSum = weightedScoreSum.add(calculateRangeScore(actualFat, fatMinG, fatMaxG).multiply(WEIGHTS.get("FAT")));

        // 2-3. 상한선 목표 (Limit)
        weightedScoreSum = weightedScoreSum.add(calculateLimitScore(actualSodium, targetSodium).multiply(WEIGHTS.get("SODIUM")));
        weightedScoreSum = weightedScoreSum.add(calculateLimitScore(actualSugar, targetSugars).multiply(WEIGHTS.get("SUGAR")));

        // 3. 최종 점수 반환 (Integer로 변환)
        // (weightedScoreSum / TOTAL_WEIGHT) 결과를 소수점 첫째 자리에서 반올림하여 정수로 반환
        return weightedScoreSum.divide(TOTAL_WEIGHT, 0, ROUNDING_MODE).intValue();
    }

    // 권장량(Min~Max) 범위 내 만점 부여 및 벗어날 시 감점 로직 (BigDecimal)
    private BigDecimal calculateRangeScore(BigDecimal actual, BigDecimal min, BigDecimal max) {
        if (actual.compareTo(min) >= 0 && actual.compareTo(max) <= 0) {
            return HUNDRED;
        }

        BigDecimal score;
        if (actual.compareTo(min) < 0) {
            // 미달 감점: 100 - ((min - actual) / min * 100)
            BigDecimal diff = min.subtract(actual);
            BigDecimal penalty = diff.divide(min, SCALE, ROUNDING_MODE).multiply(HUNDRED);
            score = HUNDRED.subtract(penalty);
        } else { // actual > max
            // 초과 감점: 100 - ((actual - max) / max * 200)
            BigDecimal diff = actual.subtract(max);
            BigDecimal penalty = diff.divide(max, SCALE, ROUNDING_MODE).multiply(OVER_RANGE_PENALTY_FACTOR);
            score = HUNDRED.subtract(penalty);
        }

        return score.max(BigDecimal.ZERO); // 점수는 0점 미만이 될 수 없음
    }

    // 상한선(Limit) 이하 만점 부여 및 초과 시 감점 로직 (BigDecimal)
    private BigDecimal calculateLimitScore(BigDecimal actual, BigDecimal limit) {
        if (actual.compareTo(limit) <= 0) {
            return HUNDRED;
        }

        // 초과 감점: 100 - ((actual - limit) / limit * 300)
        BigDecimal diff = actual.subtract(limit);
        BigDecimal penalty = diff.divide(limit, SCALE, ROUNDING_MODE).multiply(LIMIT_EXCEED_PENALTY_FACTOR);
        BigDecimal score = HUNDRED.subtract(penalty);

        return score.max(BigDecimal.ZERO);
    }
}