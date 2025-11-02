package com.fourspoons.mikkureomi.mealPicture.dto.response;

import com.fourspoons.mikkureomi.mealPicture.domain.MealPicture;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MealPictureResponseDto {

    private final Long mealPictureId;
    private final Long mealId;
    private final String url;
    private final LocalDateTime createdAt;

    public MealPictureResponseDto(MealPicture mealPicture) {
        this.mealPictureId = mealPicture.getMealPictureId();
        this.mealId = mealPicture.getMeal().getMealId(); // Meal 엔티티를 통해 ID 접근
        this.url = mealPicture.getImageUrl();
        this.createdAt = mealPicture.getCreatedAt();
    }
}