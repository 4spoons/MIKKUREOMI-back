package com.fourspoons.mikkureomi.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j // 로그 추가
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret.key}")
     private String secretKeyString;
     private Key key;

     // Bean 생성 직후 실행되어 secretKeyString을 Key 객체로 변환
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString.trim());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Secret Key 초기화 완료 (Base64 디코딩 방식)");

    }

    // TODO: 토큰 유효기간 1시간으로 변경
    //    private final long TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
    private final long TOKEN_VALIDITY = 1000L * 60 * 60 * 24; // 테스트용, 1일

    private final UserDetailsService userDetailsService;

    /**
     * 토큰에서 email을 추출하여 UserDetails를 로드하고,
     * 이를 기반으로 Spring Security의 Authentication 객체를 생성합니다.
     * @param token 유효성이 검증된 JWT
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 토큰에서 Claim을 파싱합니다.
        Claims claims = parseClaims(token);

        // Claim에서 email을 추출합니다. (generateToken에서 "email"로 저장했음)
        String email = claims.get("email", String.class);

        if (email == null) {
            throw new JwtException("토큰에서 email 정보를 찾을 수 없습니다.");
        }

        // DB 기반으로 유저 정보 조회
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // Authentication 객체(UsernamePasswordAuthenticationToken)를 생성하여 반환합니다.
        // 이 객체는 SecurityContextHolder에 저장되어 @AuthenticationPrincipal로 참조할 수 있게 됩니다.
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * (서명 일치, 만료 여부 등)
     * @param token 검증할 JWT
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱해 봅니다. 성공하면 유효한 토큰입니다.
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT Claims 문자열이 비어있습니다.", e);
        } catch (Exception e) {
            log.warn("JWT 토큰 검증 중 알 수 없는 오류가 발생했습니다.", e);
        }
        return false;
    }

    /**
     * 토큰을 파싱하여 Claims 정보를 반환합니다.
     * @param token 파싱할 JWT
     * @return Claims 객체
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
