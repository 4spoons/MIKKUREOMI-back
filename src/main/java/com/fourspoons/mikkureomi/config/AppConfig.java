package com.fourspoons.mikkureomi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
     // 비밀번호 암호화를 위한 PasswordEncoder 빈을 등록
     // (SecurityConfig에서 분리하여 순환 참조 문제 해결)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
