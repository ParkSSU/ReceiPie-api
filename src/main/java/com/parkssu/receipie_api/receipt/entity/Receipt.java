package com.parkssu.receipie_api.receipt.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Receipt 엔티티 - 영수증을 표현하는 엔티티
 * 참여자, 항목 정보를 포함한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가
    private Long id;

    @Column(nullable = false) // 가게 이름 (필수)
    private String storeName;

    @Column(nullable = false) // 영수증 날짜 (필수)
    private LocalDateTime date;

    @Column(nullable = false) // 총 금액 (필수)
    private int totalPrice;

    /**
     * 영수증에 속한 항목들 (1:N 관계)
     * - 영수증 삭제 시 관련 항목도 모두 삭제 (orphanRemoval = true)
     */
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    /**
     * 영수증의 참여자들 (1:N 관계)
     */
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceiptParticipant> participants = new ArrayList<>();
}
