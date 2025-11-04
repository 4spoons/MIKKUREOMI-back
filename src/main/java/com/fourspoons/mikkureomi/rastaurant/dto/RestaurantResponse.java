package com.fourspoons.mikkureomi.rastaurant.dto;

import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RestaurantResponse {
    private final String name;
    private final RestaurantType type;

    public RestaurantResponse(String name, RestaurantType type) {
        this.name = name;
        this.type = type;
    }
}