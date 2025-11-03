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
    PROFILE_FETCH_SUCCESS("프로필 조회가 완료되었습니다.");

    private final String message;
}
