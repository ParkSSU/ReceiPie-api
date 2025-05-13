package com.parkssu.receipie_api.global.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.receipie_api.global.filter.CachedBodyHttpServletWrapper;
import com.parkssu.receipie_api.global.jwt.JwtUtil;
import com.parkssu.receipie_api.global.service.RateLimiterService;
import com.parkssu.receipie_api.ocr.dto.ImageParsingRequest;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * ✅ RateLimiterInterceptor
 * - /ocr/analyze 요청에 대해 이미지 개수 기준으로 제한을 두는 인터셉터
 */
@RequiredArgsConstructor
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    /**
     * 인터셉터에서 Request Body를 읽고 이미지 개수를 추출하여 제한을 검증
     *
     * @param request  HttpServletRequest - 요청 객체
     * @param response HttpServletResponse - 응답 객체
     * @param handler  Object - 핸들러 (컨트롤러)
     * @return boolean - true: 요청이 허용됨, false: 제한 초과
     * @throws IOException - IOException
     */
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

        if (request.getRequestURI().startsWith("/ocr/analyze")) {
            CachedBodyHttpServletWrapper cachedRequest = (CachedBodyHttpServletWrapper) request;

            // Request Body에서 이미지 개수 추출
            ImageParsingRequest imageParsingRequest = new ObjectMapper()
                    .readValue(cachedRequest.getInputStream(), ImageParsingRequest.class);
            int imageCount = imageParsingRequest.getBase64Images().size();

            // 사용자별 버킷 가져오기
            Bucket bucket = rateLimiterService.resolveBucket(userId);

            // 현재 사용한 이미지 개수
            int usedImages = rateLimiterService.getUsedImages(userId);
            long remainingTokens = bucket.getAvailableTokens();

            // 사용 가능 이미지 개수 확인
            if (imageCount > remainingTokens) {
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("오늘 전송할 수 있는 이미지 수를 초과했습니다. 남은 이미지 전송 가능 수: " + remainingTokens);
                return false;
            }

            // 버킷에서 이미지 개수만큼 토큰 소모
            bucket.tryConsume(imageCount);

            // 로그 출력
            System.out.printf("userId: %s - 이미지 사용량: %d/%d\n", userId, usedImages + imageCount, RateLimiterService.MAX_IMAGES);
        }

        return true;
    }
}
