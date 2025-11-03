package com.fourspoons.mikkureomi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    // --- User / Auth ---
    EMAIL_ALREADY_EXISTS("이미 가입된 이메일입니다."),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    PROFILE_NOT_FOUND("프로필 정보를 찾을 수 없습니다."),

    // --- JWT & Security ---
    JWT_INVALID_TOKEN("유효하지 않은 토큰입니다."),
    ACCESS_DENIED("접근 권한이 없습니다."),

    // --- Validation ---
    INVALID_INPUT_VALUE("입력값이 올바르지 않습니다."),

    // --- MealPicture ---
    MEAL_PICTURE_NOT_FOUND("해당 Meal에 연결된 MealPicture를 찾을 수 없습니다."),

    // --- Meal ---
    MEAL_NOT_FOUND("해당 Meal 정보를 찾을 수 없습니다."),

    // --- Server ---
    INTERNAL_SERVER_ERROR("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final String message;
}
