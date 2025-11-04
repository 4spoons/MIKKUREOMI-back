package com.fourspoons.mikkureomi.mealFood.dto.response;

import lombok.Getter;
import lombok.Builder;
import java.math.BigDecimal;

// MealFood 목록의 합산 결과를 전달하기 위한 DTO
@Getter
@Builder
public class MealNutrientSummary {
    private final BigDecimal totalCalories;
    private final BigDecimal totalCarbohydrates;
    private final BigDecimal totalDietaryFiber;
    private final BigDecimal totalProtein;
    private final BigDecimal totalFat;
    private final BigDecimal totalSugars;
    private final BigDecimal totalSodium;
}
