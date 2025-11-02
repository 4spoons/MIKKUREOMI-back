package com.fourspoons.mikkureomi.mealFood.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor // final 필드를 인수로 받는 생성자를 자동 생성
public class MealFoodListResponse {

    private final int count;
    private final List<MealFoodResponseDto> mealFoods;
}