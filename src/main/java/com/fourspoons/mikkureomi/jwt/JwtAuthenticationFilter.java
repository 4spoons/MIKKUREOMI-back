package com.fourspoons.mikkureomi.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 요청에 대해 JWT 토큰을 검증하고,
 * 유효한 토큰일 경우 Spring Security Context에 인증 정보(Authentication)를 설정합니다.
 */
@Component // 이 필터를 Spring Bean으로 등록합니다.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 실제 필터링 로직입니다.
     * HTTP 요청이 오면, 헤더에서 토큰을 추출하여 유효성을 검사합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰을 추출합니다.
        String token = resolveToken(request);

        // 2. validateToken으로 토큰 유효성을 검사합니다.
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 3. 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가져옵니다.
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. SecurityContextHolder에 인증 정보를 저장합니다.
            //    (@AuthenticationPrincipal CustomUserDetails userDetails 에서 이 정보를 사용합니다)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 5. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }


//    Request Header에서 "Bearer " 접두사를 제거하고 순수 토큰만 추출합니다.
//    @param request HttpServletRequest
//    @return 추출된 토큰 (없으면 null)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 문자열 반환
        }
        return null;
    }
}