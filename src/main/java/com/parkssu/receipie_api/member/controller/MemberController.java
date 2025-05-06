package com.parkssu.receipie_api.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인된 사용자 정보를 조회하는 API
 */
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    /**
     * 인증된 사용자의 이메일 반환
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyEmail(Authentication authentication) {
        String email = (String) authentication.getPrincipal(); // JwtAuthenticationFilter에서 설정한 이메일
        return ResponseEntity.ok(email);
    }
}
