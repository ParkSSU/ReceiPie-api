package com.parkssu.receipie_api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

    /**
     * 인증된 사용자의 이메일 반환
     */
    @GetMapping("/me")
    @Operation(
            summary = "내 이메일 조회",
            description = """
                로그인된 사용자의 이메일을 반환합니다. <br/>
                JWT를 통해 인증된 사용자만 접근 가능합니다.
                """
    )
    public ResponseEntity<String> getMyEmail(Authentication authentication) {
        String email = (String) authentication.getPrincipal(); // JwtAuthenticationFilter에서 설정한 이메일
        return ResponseEntity.ok(email);
    }
}
