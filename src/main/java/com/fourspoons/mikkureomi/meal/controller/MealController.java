package com.fourspoons.mikkureomi.meal.controller;


import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.meal.dto.response.MealResponseDto;
import com.fourspoons.mikkureomi.meal.service.MealService;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;
    private final ProfileService profileService;

    // 엔드포인트 1: Meal ID로 상세 조회
    @GetMapping("/{mealId}/detail")
    public ResponseEntity<ApiResponse<MealResponseDto>> getMealDetailById(@AuthenticationPrincipal CustomUserDetails userDetails,  @PathVariable Long mealId) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        MealResponseDto responseDto = mealService.getMealDetailById(profileId, mealId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MEAL_SUCCESS.getMessage(), responseDto));
    }

    // 엔드포인트 2: 날짜로 리스트 조회
    @GetMapping("/by-date")
    public ResponseEntity<ApiResponse<List<MealResponseDto>>>getMealsByDate(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam("date") LocalDate date) {
        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());
        List<MealResponseDto> responseList = mealService.getMealsByDate(profileId, date);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.GET_MEAL_SUCCESS.getMessage(), responseList));
    }

}