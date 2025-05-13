package com.parkssu.receipie_api.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.receipie_api.global.interceptor.RateLimiterInterceptor;
import com.parkssu.receipie_api.global.jwt.JwtUtil;
import com.parkssu.receipie_api.global.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정 클래스.
 * 인터셉터를 등록하는 역할을 담당.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;


    /**
     * 생성자 주입
     *
     * @param rateLimiterService 요청 제한 서비스
     * @param jwtUtil JWT 유틸리티
     * @param objectMapper JSON 파싱을 위한 ObjectMapper
     */
    @Autowired
    public WebConfig(RateLimiterService rateLimiterService, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.rateLimiterService = rateLimiterService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 인터셉터를 특정 엔드포인트에만 적용하도록 등록.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimiterInterceptor(rateLimiterService, jwtUtil))
                .addPathPatterns("/ocr/analyze")
                .order(1);  // 필터보다 뒤에 실행되도록 설정
    }

}
