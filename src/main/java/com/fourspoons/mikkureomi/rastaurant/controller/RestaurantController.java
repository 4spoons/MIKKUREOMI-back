package com.fourspoons.mikkureomi.rastaurant.controller;

import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import com.fourspoons.mikkureomi.rastaurant.dto.RestaurantResponse;
import com.fourspoons.mikkureomi.rastaurant.dto.RestaurantResponseSpecific;
import com.fourspoons.mikkureomi.rastaurant.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    // 생성자 주입 생략
    RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/nearby")
    public List<RestaurantResponse> getNearbyRestaurants(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") int radius,
            @RequestParam(required = false) Optional<RestaurantType> type
    ) {
        return restaurantService.getRestaurants(lat, lng, radius, type);
    }

    @GetMapping("/nearby/specific")
    public List<RestaurantResponseSpecific> getNearbyRestaurantsSpecific(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1000") int radius,
            @RequestParam(required = false) Optional<RestaurantType> type
    ) {
        return restaurantService.getSpecificRestaurants(lat, lng, radius, type);
    }
}
