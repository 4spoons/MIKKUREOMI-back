package com.fourspoons.mikkureomi.mealPicture.dto.request;

import com.fourspoons.mikkureomi.mealFood.dto.request.MealFoodRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealFinalSaveRequestDto {

    private List<MealFoodRequestDto> mealFoodList;

}
