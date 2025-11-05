package com.fourspoons.mikkureomi.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    // USER & Profile
    SIGNUP_SUCCESS("회원가입이 완료되었습니다."),
    LOGIN_SUCCESS("로그인이 완료되었습니다."),
    LOGOUT_SUCCESS("로그아웃이 완료되었습니다."),
    PWD_UPDATE_SUCCESS("비밀번호가 성공적으로 변경되었습니다."),
    DELETE_ACCOUNT_SUCCESS("회원 탈퇴가 완료되었습니다."),
    PROFILE_UPDATED("프로필이 수정되었습니다."),
    PROFILE_FETCH_SUCCESS("프로필 조회가 완료되었습니다."),

    // MealFood
    CREATE_MEAL_FOOD_SUCCESS("음식 리스트가 저장되었습니다."),
    GET_MEAL_FOODS_SUCCESS("음식 리스트 조회를 완료하였습니다."),

    // MealPicture
    RECOGNIZE_FOODS_SUCCESS("음식 인식에 성공하였습니다"),
    SAVE_FINAL_MEAL_SUCCESS("사진 및 음식 리스트가 저장되었습니다"),
    GET_PICTURE_SUCCESS("사진 조회가 완료되었습니다"),

    // Meal
    GET_MEAL_SUCCESS("식사 정보 조회가 완료되었습니다."),

    // DailyReport
    GET_DAILY_REPORT_SUCCESS("데일리 리포트 조회가 완료되었습니다."),
    DELETE_DAILY_REPORT_SUCCESS("데일리 리포트가 삭제되었습니다."),

    // Food
    SYNC_DATA_SUCCESS("공공데이터 동기화가 완료되었습니다."),
    SEARCH_FOOD_SUCCESS("음식 검색이 완료되었습니다.");


    private final String message;
}
