package com.fourspoons.mikkureomi.mealPicture.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecognitionDto {

    // JSON의 "detected_foods" 필드와 매핑
    @JsonProperty("detected_foods")
    private List<RecognizedFood> detectedFoods;
}