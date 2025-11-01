package com.fourspoons.mikkureomi.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//    private final long TOKEN_VALIDITY = 1000L * 60 * 60; // 1시간
    private final long TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 테스트용, 7일


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
}
