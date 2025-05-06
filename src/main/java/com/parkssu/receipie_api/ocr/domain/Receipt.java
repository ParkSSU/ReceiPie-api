package com.parkssu.receipie_api.ocr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Receipt는 하나의 영수증 정보를 표현하는 도메인 모델입니다.
 * 클라이언트 응답에 직접 사용되기보다, 내부 로직에서 처리되는 객체입니다.
 */
@Getter // getter 자동 생성
@Setter // setter 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 전체 필드 초기화 생성자 자동 생성
public class Receipt {
    private List<Item> items;       // 품목 리스트
    @JsonProperty("total_price")  // JSON 필드명(total_price) ↔ 자바 필드명(totalPrice) 매핑
    private int totalPrice;         // 총합 금액
    private String store;           // 상호명
    private String date;            // 날짜 (예: 2024-04-25)
}
