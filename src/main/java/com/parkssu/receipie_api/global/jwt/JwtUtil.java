package com.parkssu.receipie_api.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT를 생성하고 검증하는 유틸리티 클래스
 * - 전역(global)에서 사용 가능
 * - 주로 로그인 이후 사용자 인증 처리에 사용됨
 */
@Component
public class JwtUtil {

    // application.properties에 정의한 시크릿 키를 주입
    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    // 토큰 유효 시간: 1시간 (1000ms * 60초 * 60분 * 24 * 60 )
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 60;

    // 생성자 이후 실행되어 key 객체를 초기화
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // BASE64로 디코딩
        this.key = Keys.hmacShaKeyFor(keyBytes); // 디코딩된 값으로 HMAC 키 생성
    }

    /**
     * JWT 토큰을 생성한다.
     * @param subject 사용자 식별값 (주로 email)
     * @return 생성된 JWT 문자열
     */
    public String createToken(String subject) {
        return Jwts.builder()
                .setSubject(subject) // 사용자 식별자 (이메일 등)
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 키로 서명
                .compact(); // 최종 JWT 문자열 생성
    }

    /**
     * 토큰에서 사용자 식별 정보(subject)를 추출한다.
     */
    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * JWT 토큰을 검증하고 유효한지 확인한다.
     */
    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("지원하지 않는 JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 형식의 JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("JWT 서명 검증 실패: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 파라미터 누락 또는 오류");
        }
        return false;
    }

    /**
     * JWT 토큰에서 Claims(데이터 덩어리)를 추출한다.
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료된 토큰도 claims를 꺼낼 수 있음
        }
    }

    /**
     * JWT 토큰에서 userId를 추출한다.
     *
     * @param token - JWT 토큰
     * @return String - 사용자 ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject(); // userId가 Subject에 저장되어 있다고 가정
    }
}
