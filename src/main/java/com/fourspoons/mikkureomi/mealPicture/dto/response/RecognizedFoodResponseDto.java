package com.fourspoons.mikkureomi.mealPicture.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RecognizedFoodResponseDto {

    private final Long foodId;
    private final String foodName;
}
