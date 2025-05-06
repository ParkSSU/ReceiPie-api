package com.parkssu.receipie_api.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.receipie_api.global.jwt.JwtUtil;
import com.parkssu.receipie_api.member.entity.Member;
import com.parkssu.receipie_api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용

    // application.properties 에서 불러오는 값들
    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    /**
     * 카카오 인가 코드로부터 JWT 토큰까지 리턴하는 메서드
     */
    public String kakaoLogin(String code) throws Exception {
        // 1단계: 인가 코드 → access_token 요청
        String accessToken = requestAccessToken(code);

        // 2단계: access_token → 사용자 정보 요청
        JsonNode userInfo = requestUserInfo(accessToken);

        String email = userInfo.get("kakao_account").get("email").asText();
        String nickname = userInfo.get("properties").get("nickname").asText();

        // 3단계: DB에 사용자 저장 (없으면)
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member = optionalMember.orElseGet(() -> {
            return memberRepository.save(Member.builder()
                    .email(email)
                    .nickname(nickname)
                    .build());
        });

        // 4단계: JWT 발급
        return jwtUtil.createToken(member.getEmail());
    }

    /**
     * 인가 코드로 access_token 요청
     */
    private String requestAccessToken(String code) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                tokenUri, request, String.class);

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /**
     * access_token으로 사용자 정보 요청
     */
    private JsonNode requestUserInfo(String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization: Bearer access_token

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, request, String.class);

        return objectMapper.readTree(response.getBody());
    }
}
