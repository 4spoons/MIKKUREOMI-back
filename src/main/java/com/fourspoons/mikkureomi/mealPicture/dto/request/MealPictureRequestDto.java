package com.fourspoons.mikkureomi.mealPicture.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealPictureRequestDto {

    private String url;
    private Long mealId;
}