package com.fourspoons.mikkureomi.config;

import com.fourspoons.mikkureomi.jwt.JwtAccessDeniedHandler;
import com.fourspoons.mikkureomi.jwt.JwtAuthenticationEntryPoint;
import com.fourspoons.mikkureomi.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정 클래스
 * - JWT 기반의 인증/인가를 위한 보안 설정을 구성합니다.
 * - API 서버에 적합한 Stateless 설정을 적용합니다.
 */
@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor // final 필드 주입을 위해 추가
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 빈을 등록합니다.
     * @return CorsConfigurationSource 인스턴스
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin (프론트엔드 서버) 목록
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",       // 로컬 개발 환경
                "http://127.0.0.1:3000",      // 로컬 개발 환경
                "https://your-production-domain.com" // TODO: [배포] 실제 프로덕션 프론트엔드 도메인으로 교체
        ));

        // 허용할 HTTP 메서드 목록
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));

        // 허용할 HTTP 헤더 목록
        configuration.setAllowedHeaders(List.of(
                "Authorization",  // JWT 토큰 인증 헤더
                "Content-Type",   // 요청/응답 컨텐츠 타입 헤더
                "X-Requested-With"
        ));

        // 클라이언트(브라우저)에 노출할 헤더 목록
        // (예: 응답 헤더에 JWT 토큰을 담아 보낼 경우)
        configuration.setExposedHeaders(List.of("Authorization"));

        // [중요] 자격 증명(쿠키, 인증 헤더 등) 허용 여부
        // true로 설정 시, setAllowedOrigins에 "*" 와일드카드를 사용할 수 없습니다.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // "/**" (모든 경로)에 대해 위에서 정의한 CORS 정책을 적용합니다.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Spring Security의 메인 필터 체인을 설정합니다.
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 인스턴스
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS 설정 적용 (corsConfigurationSource 빈 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF 보호 비활성화 (Stateless JWT 사용 시 불필요)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. 세션 관리 정책을 STATELESS로 설정 (세션 사용 안 함)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. 기본 로그인 폼 및 HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 5. URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // [1순위] OPTIONS (Preflight) 요청은 인증 여부와 관계없이 모두 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // [2순위] 회원가입, 로그인 API는 인증 없이 접근 허용
                        .requestMatchers(HttpMethod.POST, "/api/users/signup", "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/meal-foods/**", "/api/meal-pictures/**").permitAll() // 테스트
                        .requestMatchers(HttpMethod.GET, "/api/meal-foods/**", "/api/meal-pictures/**", "/api/meals/**").permitAll() // 테스트


                        // [3순위] 위에서 정의한 경로 외의 모든 요청은 반드시 인증(토큰) 필요
                        .anyRequest().authenticated()
                ) .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())   // 인증 실패 처리
                        .accessDeniedHandler(new JwtAccessDeniedHandler())             // 인가 실패 처리
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
