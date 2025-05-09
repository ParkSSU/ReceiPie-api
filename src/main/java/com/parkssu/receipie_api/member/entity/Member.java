package com.parkssu.receipie_api.member.entity;

import com.parkssu.receipie_api.receipt.entity.Receipt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String nickname; // 카카오 닉네임 (선택)

    /**
     * 해당 사용자가 총무로 등록된 영수증 목록
     */
    @OneToMany(mappedBy = "member")
    @JsonIgnore // 순환 참조 방지
    private List<Receipt> receipts = new ArrayList<>();
}