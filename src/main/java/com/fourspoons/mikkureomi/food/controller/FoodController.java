package com.fourspoons.mikkureomi.food.controller;

import com.fourspoons.mikkureomi.food.dto.response.FoodSearchResponse;
import com.fourspoons.mikkureomi.food.service.FoodService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    // 공공데이터 전체 DB 동기화
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<Void>> syncData() {
        foodService.syncFoodDataFromAPI();
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.SYNC_DATA_SUCCESS.getMessage()));
    }

    // 부분 검색
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<FoodSearchResponse>> searchFood(@RequestParam("name") String name) {
        FoodSearchResponse foodList = foodService.searchFoodByName(name);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.SEARCH_FOOD_SUCCESS.getMessage(), foodList));
    }
}
