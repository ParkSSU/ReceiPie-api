package com.parkssu.receipie_api.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.receipie_api.global.jwt.JwtUtil;
import com.parkssu.receipie_api.global.service.RateLimiterService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 사용자별 요청 제한을 관리하는 인터셉터.
 * 요청당 1개의 토큰을 소비하도록 설정.
 */
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized - JWT Token is missing or invalid");
            return false;
        }

        String jwtToken = token.substring(7);
        String userId;
        try {
            userId = jwtUtil.getUserIdFromToken(jwtToken);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Unauthorized - Invalid JWT Token");
            return false;
        }

        // 사용자별 Bucket 가져오기
        Bucket bucket = rateLimiterService.resolveBucket(userId);


        // 요청당 1개 토큰 소비
        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too Many Requests - Daily limit reached");
            return false;
        }

        int usedRequests = rateLimiterService.getUsedRequests(userId);
        long maxTokens = RateLimiterService.MAX_TOKENS; // 서비스의 MAX_TOKENS 값을 참조
        // 로그로 요청 횟수 출력
        System.out.printf("userId: %s - 사용량: %d/%d (1개 요청당 1토큰 소모)%n", userId, usedRequests, maxTokens);


        return true;
    }
}
