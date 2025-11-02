package com.fourspoons.mikkureomi.profile.controller;

import com.fourspoons.mikkureomi.constants.ResponseMessage;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.profile.dto.ProfileResponseDto;
import com.fourspoons.mikkureomi.profile.dto.ProfileUpdateRequestDto;
import com.fourspoons.mikkureomi.profile.service.ProfileService;
import com.fourspoons.mikkureomi.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping()
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        ProfileResponseDto profileResponseDto = profileService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.PROFILE_FETCH_SUCCESS.getMessage(), profileResponseDto));
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ProfileUpdateRequestDto dto
    ) {
        Long userId = userDetails.getUser().getUserId();
        ProfileResponseDto response = profileService.updateProfile(userId, dto);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.PROFILE_UPDATED.getMessage(), response)
        );
    }
}
