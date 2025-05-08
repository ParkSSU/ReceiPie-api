package com.parkssu.receipie_api.receipt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ItemBuyer 엔티티 - 특정 항목을 구매한 사용자 정보
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class ItemBuyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가
    private Long id;

    @Column(nullable = false) // 구매자 이름 (필수)
    private String username;

    @Column(nullable = false) // 구매한 수량 (필수)
    private int count;

    /**
     * 항목과 다대일 관계 설정 (ItemBuyer는 하나의 Item에 속함)
     */
    @ManyToOne
    @JoinColumn(name = "item_id") // Item과 조인
    @JsonIgnore
    private Item item;
}
