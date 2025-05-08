package com.parkssu.receipie_api.receipt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ReceiptParticipant 엔티티 - 영수증의 참여자 정보
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ReceiptParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가
    private Long id;

    @Column(nullable = false) // 참여자 이름 (필수)
    private String username;

    /**
     * 영수증과 다대일 관계 설정
     */
    @ManyToOne
    @JoinColumn(name = "receipt_id") // Receipt와 조인
    @JsonIgnore
    private Receipt receipt;
}
