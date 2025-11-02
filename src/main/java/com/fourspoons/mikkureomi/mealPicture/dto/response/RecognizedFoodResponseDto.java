package com.fourspoons.mikkureomi.mealPicture.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RecognizedFoodResponseDto {

    private final String foodName;
    private final BigDecimal quantity;
    private final BigDecimal calories;
    private final BigDecimal carbohydrates;
    private final BigDecimal dietaryFiber;
    private final BigDecimal protein;
    private final BigDecimal fat;
    private final BigDecimal sugars;
}
