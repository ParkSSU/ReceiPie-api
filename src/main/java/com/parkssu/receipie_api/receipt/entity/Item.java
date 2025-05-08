package com.parkssu.receipie_api.receipt.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Item 엔티티 - 영수증 내 항목 정보를 저장
 * 구매자 정보를 포함한다.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 증가
    private Long id;

    @Column(nullable = false) // 항목명 (필수)
    private String name;

    @Column(nullable = false) // 수량 (필수)
    private int count;

    @Column(nullable = false) // 가격 (필수)
    private int price;

    /**
     * 영수증과 다대일 관계 설정 (Item은 하나의 Receipt에 속함)
     */
    @ManyToOne
    @JoinColumn(name = "receipt_id") // 영수증 ID와 조인
    @JsonIgnore
    private Receipt receipt;

    /**
     * 해당 항목을 구매한 사람들 (1:N 관계)
     */
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemBuyer> buyers = new ArrayList<>();
}
