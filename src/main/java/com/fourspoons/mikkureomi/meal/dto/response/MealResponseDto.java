package com.fourspoons.mikkureomi.meal.dto.response;

import com.fourspoons.mikkureomi.meal.domain.Meal;
import lombok.Getter;

@Getter
public class MealResponseDto {

    private final Long mealId;

    // Meal 엔티티를 받아 DTO를 생성하는 생성자
    public MealResponseDto(Meal meal) {
        this.mealId = meal.getMealId();
    }
}