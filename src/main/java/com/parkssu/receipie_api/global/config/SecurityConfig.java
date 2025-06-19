package com.parkssu.receipie_api.global.config;

import com.parkssu.receipie_api.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Spring Security 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화 (커스텀 설정은 아래 Bean 참고)
                .cors(Customizer.withDefaults())
                // CSRF 비활성화 (REST API 서버에서는 비활성화 권장)
                .csrf(csrf -> csrf.disable())
                // Form 로그인 및 기본 인증 제거
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                // 인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/api/oauth/**",      // ✅ 카카오 콜백 포함
                                "/test.html",
                                "/login-test.html",
                                "/h2-console/**"
                        ).permitAll() // 인증 없이 허용
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // 예외 처리: 인증 실패 → 401, 인가 실패 → 403
                .exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 401
                )

                // JWT 필터 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Spring Security 필터 체인 자체를 아예 무시할 경로 지정 (JWT 필터도 타지 않음)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/api-docs/**",
                        "/api/oauth/**", // 🔥 카카오 로그인 콜백 경로는 완전 무시 처리 (403 방지 핵심)
                        "/test.html",
                        "/login-test.html",
                        "/h2-console/**"
                );
    }

    // 전역 CORS 설정 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 Origin 설정 (배포용 + 로컬 개발용)
        config.setAllowedOrigins(List.of(
                "https://recei-pie-fe.vercel.app",   // 배포용 프론트 주소
                "http://localhost:5173"              // 개발용 Vite 주소
        ));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 Header
        config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용

        // 인증 정보 포함 여부 (쿠키 포함 요청 가능)
        config.setAllowCredentials(true);

        // CORS 설정 적용 범위
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

