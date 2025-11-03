package com.fourspoons.mikkureomi.mealFood.controller;


import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.mealFood.dto.request.MealCreateRequestDto;
import com.fourspoons.mikkureomi.mealFood.dto.response.MealFoodListResponse;
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

    /** 1. Meal 생성 및 음식 목록 (MealFood) 등록 (POST) */
    // 사용자가 사진 없이 음식 목록을 보낼 때, Meal과 MealFood를 동시에 생성합니다.
    @PostMapping
    public ResponseEntity<ApiResponse<List<MealFoodResponseDto>>> createMealWithFoods(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MealCreateRequestDto requestDto) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        List<MealFoodResponseDto> responseList = mealFoodService.createMealWithFoods(profileId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.CREATE_MEAL_FOOD_SUCCESS.getMessage(), responseList)); // 201 Created
    }

    /** 2. 특정 Meal에 속한 MealFood 목록 조회 (GET List by Meal ID) */
    @GetMapping("/by-meal/{mealId}")
    public ResponseEntity<ApiResponse<MealFoodListResponse>> getMealFoodsByMealId(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long mealId) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        MealFoodListResponse response = mealFoodService.getMealFoodsByMealId(profileId, mealId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MEAL_FOODS_SUCCESS.getMessage(), response)); // 200 OK
    }

//    /** 3. 특정 MealFood 단일 수정 (PUT) */
//    @PutMapping("/{mealFoodId}")
//    public ResponseEntity<MealFoodResponseDto> updateMealFood(
//            @PathVariable Long mealFoodId,
//            @RequestBody MealFoodRequestDto requestDto) {
//        MealFoodResponseDto responseDto = mealFoodService.updateMealFood(mealFoodId, requestDto);
//        return ResponseEntity.ok(responseDto); // 200 OK
//    }

//    /** 4. 특정 MealFood 삭제 (DELETE) */
//    @DeleteMapping("/{mealFoodId}")
//    public ResponseEntity<Void> deleteMealFood(@PathVariable Long mealFoodId) {
//        mealFoodService.deleteMealFood(mealFoodId);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
//    }
}