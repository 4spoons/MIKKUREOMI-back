package com.fourspoons.mikkureomi.food.controller;

import com.fourspoons.mikkureomi.food.dto.response.FoodResponse;
import com.fourspoons.mikkureomi.food.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/search")
    public FoodResponse getFoodInfo(@RequestParam("name") String name) {
        return foodService.searchFoodByName(name);
    }
}
