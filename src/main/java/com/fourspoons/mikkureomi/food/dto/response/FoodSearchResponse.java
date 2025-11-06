package com.fourspoons.mikkureomi.food.dto.response;

import com.fourspoons.mikkureomi.food.domain.Food;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FoodSearchResponse {

    private int count;
    private String query;
    private LocalDateTime timestamp;
    private List<Food> foods;
}