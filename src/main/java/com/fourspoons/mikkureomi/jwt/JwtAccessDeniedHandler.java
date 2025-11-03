package com.fourspoons.mikkureomi.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.exception.ErrorMessage;
import com.fourspoons.mikkureomi.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            response.setContentType("application/json;charset=UTF-8");

            ApiResponse<Void> body = ApiResponse.fail(ErrorMessage.ACCESS_DENIED.getMessage());
            String json = new ObjectMapper().writeValueAsString(body);

            response.getWriter().write(json);

        }
}