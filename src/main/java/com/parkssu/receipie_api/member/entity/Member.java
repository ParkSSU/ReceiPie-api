package com.parkssu.receipie_api.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity   // JPA에서 데이터베이스 테이블로 인식
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 카카오 이메일

    @Column(nullable = false)
    private String nickname; // 카카오 닉네임
}