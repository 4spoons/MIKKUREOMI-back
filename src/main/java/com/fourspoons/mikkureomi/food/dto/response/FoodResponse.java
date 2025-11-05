package com.fourspoons.mikkureomi.food.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodResponse {
    private FoodHeader header;
    private FoodBody body;
}
