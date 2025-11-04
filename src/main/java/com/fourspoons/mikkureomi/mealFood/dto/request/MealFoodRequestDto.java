package com.fourspoons.mikkureomi.mealFood.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealFoodRequestDto {

    private String foodName;
    private BigDecimal quantity;
    private BigDecimal calories;
    private BigDecimal carbohydrates;
    private BigDecimal dietaryFiber;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal sugars;
    private BigDecimal sodium;
}