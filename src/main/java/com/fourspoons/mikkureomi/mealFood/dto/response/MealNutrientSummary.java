package com.fourspoons.mikkureomi.mealFood.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class MealNutrientSummary {

    private final BigDecimal calories;
    private final BigDecimal carbohydrates;
    private final BigDecimal dietaryFiber;
    private final BigDecimal protein;
    private final BigDecimal fat;
    private final BigDecimal sugars;
    private final BigDecimal sodium;

    // 기본값 0으로 초기화
    public static MealNutrientSummary empty() {
        return new MealNutrientSummary(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    // 영양 성분 누적용 메서드
    public MealNutrientSummary add(MealNutrientSummary other) {
        return new MealNutrientSummary(
                this.calories.add(other.calories),
                this.carbohydrates.add(other.carbohydrates),
                this.dietaryFiber.add(other.dietaryFiber),
                this.protein.add(other.protein),
                this.fat.add(other.fat),
                this.sugars.add(other.sugars),
                this.sodium.add(other.sodium)
        );
    }
}
