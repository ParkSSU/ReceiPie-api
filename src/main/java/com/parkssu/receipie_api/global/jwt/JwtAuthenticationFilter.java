package com.parkssu.receipie_api.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 확인하고 인증 객체를 등록하는 필터
 * 요청당 한 번 실행되며, Security Filter Chain에 등록되어야 동작함
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 값 가져오기
        String authHeader = request.getHeader("Authorization");

        // 2. Bearer 토큰 형식이 아닌 경우 필터 통과
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 제거 후 JWT 추출
        String token = authHeader.substring(7); // // "Bearer "의 길이는 7

        // 4. 유효한 토큰인지 검사
        if (jwtUtil.isValidToken(token)) {
            String subject = jwtUtil.extractSubject(token); // 주로 email

            // 5. 인증 객체 생성 (우리는 간단히 이메일만 인증 정보로 씀)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(subject, null, null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. 인증 정보를 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 7. 다음 필터로 계속 진행
        filterChain.doFilter(request, response);
    }
}
