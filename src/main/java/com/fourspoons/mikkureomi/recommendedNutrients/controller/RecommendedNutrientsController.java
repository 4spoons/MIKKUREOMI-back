package com.fourspoons.mikkureomi.recommendedNutrients.controller;

import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.recommendedNutrients.dto.RecommendedNutrientsResponseDto;
import com.fourspoons.mikkureomi.recommendedNutrients.service.RecommendedNutrientsService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import com.fourspoons.mikkureomi.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendation")
public class RecommendedNutrientsController {
    private final RecommendedNutrientsService recommendedNutrientsService;
    private final ProfileService profileService;

    @GetMapping("/nutrients")
    public ResponseEntity<ApiResponse<RecommendedNutrientsResponseDto>> getRecommendedNutrients(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long profileId = profileService.getProfileId(userDetails.getUser().getUserId());

        RecommendedNutrientsResponseDto responseDto = recommendedNutrientsService.getRecommendedNutrients(profileId);

        // 3. 성공 응답 반환
        return ResponseEntity.ok(ApiResponse.success(
                ResponseMessage.RECOMMENDATION_FETCH_SUCCESS.getMessage(),
                responseDto
        ));
    }
}
