package com.fourspoons.mikkureomi.meal.dto.response;

import com.fourspoons.mikkureomi.meal.domain.Meal;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealPicture.dto.response.MealPictureResponseDto;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MealResponseDto {

    private final Long mealId;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private final int mealFoodCount;

    // 연결된 MealFood 목록
    private final List<MealFoodResponseDto> mealFoods;

    // 연결된 MealPicture 정보
    private final MealPictureResponseDto mealPicture;

    public MealResponseDto(Meal meal) {
        this.mealId = meal.getMealId();
        this.createdAt = meal.getCreatedAt();
        this.modifiedAt = meal.getModifiedAt();

        // MealFood 매핑
        if (meal.getMealFoods() != null) {
            this.mealFoods = meal.getMealFoods().stream()
                    .map(MealFoodResponseDto::new)
                    .collect(Collectors.toList());
            this.mealFoodCount = this.mealFoods.size();
        } else {
            this.mealFoods = List.of();
            this.mealFoodCount = 0;
        }

        // MealPicture 매핑
        if (meal.getMealPicture() != null) {
            this.mealPicture = new MealPictureResponseDto(meal.getMealPicture());
        } else {
            this.mealPicture = null;
        }
    }
}