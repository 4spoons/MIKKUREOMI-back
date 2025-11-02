package com.fourspoons.mikkureomi.user.controller;

import com.fourspoons.mikkureomi.response.ResponseMessage;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.user.dto.*;
import com.fourspoons.mikkureomi.user.service.UserService;
import com.fourspoons.mikkureomi.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequestDto dto) {
        userService.signUp(dto);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.SIGNUP_SUCCESS.getMessage())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody LoginRequestDto dto) {
        LoginResponseDto loginResponseDto = userService.login(dto);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.LOGIN_SUCCESS.getMessage(),
                        loginResponseDto)
        );
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdatePasswordRequestDto dto
    ) {
        Long userId = userDetails.getUser().getUserId();
        userService.updatePassword(userId, dto);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.PWD_UPDATE_SUCCESS.getMessage())
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordRequestDto dto) {
        Long userId = userDetails.getUser().getUserId();
        userService.deleteUser(userId, dto);
        return ResponseEntity.ok(
                ApiResponse.success(ResponseMessage.DELETE_ACCOUNT_SUCCESS.getMessage())
        );
    }
}
