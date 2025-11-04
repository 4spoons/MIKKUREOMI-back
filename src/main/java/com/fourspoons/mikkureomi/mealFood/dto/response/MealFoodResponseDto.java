package com.fourspoons.mikkureomi.mealFood.dto.response;

import com.fourspoons.mikkureomi.mealFood.domain.MealFood;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class MealFoodResponseDto {

    private final Long mealFoodId;
    private final Long mealId;
    private final String foodName;
    private final BigDecimal quantity;
    private final BigDecimal calories;
    private final BigDecimal carbohydrates;
    private final BigDecimal dietaryFiber;
    private final BigDecimal protein;
    private final BigDecimal fat;
    private final BigDecimal sugars;
    private final BigDecimal sodium;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public MealFoodResponseDto(MealFood mealFood) {
        this.mealFoodId = mealFood.getMealFoodId();
        this.mealId = mealFood.getMeal().getMealId();
        this.foodName = mealFood.getFoodName();
        this.quantity = mealFood.getQuantity();
        this.calories = mealFood.getCalories();
        this.carbohydrates = mealFood.getCarbohydrates();
        this.dietaryFiber = mealFood.getDietaryFiber();
        this.protein = mealFood.getProtein();
        this.fat = mealFood.getFat();
        this.sugars = mealFood.getSugars();
        this.sodium = mealFood.getSodium();
        this.createdAt = mealFood.getCreatedAt();
        this.modifiedAt = mealFood.getModifiedAt();
    }

}