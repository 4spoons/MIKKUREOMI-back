package com.fourspoons.mikkureomi.mealFood.controller;


import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealCreateRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodResponseDto;
import com.fourspoons.mikkureomi.mealFood.service.MealFoodService;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-foods")
@RequiredArgsConstructor
public class MealFoodController {

    private final MealFoodService mealFoodService;
    private final ProfileService profileService;

    // 1. Meal 생성 및 음식 목록 (MealFood) 등록 (POST)
    @PostMapping
    public ResponseEntity<ApiResponse<List<MealFoodResponseDto>>> createMealWithFoods(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MealCreateRequestDto requestDto) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        List<MealFoodResponseDto> responseList = mealFoodService.createMealWithFoods(profileId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.CREATE_MEAL_FOOD_SUCCESS.getMessage(), responseList)); // 201 Created
    }

}