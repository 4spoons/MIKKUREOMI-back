package com.fourspoons.mikkureomi.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<Void> body = ApiResponse.fail(ErrorMessage.JWT_INVALID_TOKEN.getMessage());
        String json = new ObjectMapper().writeValueAsString(body);

        response.getWriter().write(json);
    }
}
