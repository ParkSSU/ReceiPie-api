package com.parkssu.receipie_api.member.controller;

import com.parkssu.receipie_api.member.dto.TokenResponse;
import com.parkssu.receipie_api.member.service.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


/**
 * KakaoController - 카카오 OAuth 로그인 관련 컨트롤러
 */
@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
@Tag(name = "Kakao OAuth", description = "카카오 로그인 및 인증 API")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://recei-pie-fe.vercel.app"
}, maxAge = 3600)
public class KakaoController {

    private final KakaoOAuthService kakaoOAuthService;

    @Operation(
            summary = "카카오 로그인 콜백",
            description = """
            카카오 인가 코드를 받아서 JWT 토큰을 발급합니다. <br/>
            클라이언트에서 카카오 로그인 후, 인가 코드를 이 엔드포인트로 전달합니다.
            """,
            tags = {"Kakao OAuth"}
    )
    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> kakaoCallback(
            @RequestParam("code") String code) throws Exception {
        String jwt = kakaoOAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new TokenResponse(jwt));
    }
}
