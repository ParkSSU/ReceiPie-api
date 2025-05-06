package com.parkssu.receipie_api.member.controller;

import com.parkssu.receipie_api.member.dto.TokenResponse;
import com.parkssu.receipie_api.member.service.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> kakaoCallback(@RequestParam("code") String code) throws Exception {
        String jwt = kakaoOAuthService.kakaoLogin(code);
        return ResponseEntity.ok(new TokenResponse(jwt));
    }
}

