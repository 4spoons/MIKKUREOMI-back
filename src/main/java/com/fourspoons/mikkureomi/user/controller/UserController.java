package com.fourspoons.mikkureomi.user.controller;

import com.fourspoons.mikkureomi.constants.ResponseMessage;
import com.fourspoons.mikkureomi.user.dto.LoginRequestDto;
import com.fourspoons.mikkureomi.user.dto.LoginResponseDto;
import com.fourspoons.mikkureomi.user.service.UserService;
import com.fourspoons.mikkureomi.user.dto.SignUpRequestDto;
import common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
