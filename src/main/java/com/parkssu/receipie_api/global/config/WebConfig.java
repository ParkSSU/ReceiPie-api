package com.parkssu.receipie_api.global.config;

import com.parkssu.receipie_api.global.filter.OCRRequestFilter;
import com.parkssu.receipie_api.global.interceptor.RateLimiterInterceptor;
import com.parkssu.receipie_api.global.jwt.JwtUtil;
import com.parkssu.receipie_api.global.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * ✅ WebConfig
 * - Spring의 Interceptor 및 Filter를 등록하는 클래스
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    /**
     * Interceptor 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimiterInterceptor(rateLimiterService, jwtUtil))
                .addPathPatterns("/ocr/analyze");
    }

    /**
     * Filter 등록 - OCRRequestFilter만 적용
     */
    public void onStartup(ServletContext servletContext) throws ServletException {
        FilterRegistration.Dynamic filter = servletContext.addFilter("OCRRequestFilter", new OCRRequestFilter());
        filter.addMappingForUrlPatterns(null, false, "/ocr/analyze");
    }
}
