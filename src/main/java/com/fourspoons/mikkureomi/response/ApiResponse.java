package com.fourspoons.mikkureomi.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 일관된 API 응답 포맷 유지를 위한 클래스
@Getter
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse success(String message, T data) {
        return of(true, message, data);
    }

    public static <T> ApiResponse success(String message) {
        return of(true, message, null);
    }

    public static <T> ApiResponse fail(String message) {
        return of(false, message, null);
    }
}
