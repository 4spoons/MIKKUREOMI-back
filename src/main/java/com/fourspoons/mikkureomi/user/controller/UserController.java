package com.fourspoons.mikkureomi.user.controller;

import com.fourspoons.mikkureomi.constants.ResponseMessage;
import com.fourspoons.mikkureomi.jwt.CustomUserDetails;
import com.fourspoons.mikkureomi.user.dto.LoginRequestDto;
import com.fourspoons.mikkureomi.user.dto.LoginResponseDto;
import com.fourspoons.mikkureomi.user.dto.UpdatePasswordRequestDto;
import com.fourspoons.mikkureomi.user.service.UserService;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import common.ApiResponse;
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
                ApiResponse.success("비밀번호가 성공적으로 변경되었습니다.")
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getUserId();
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }
}
