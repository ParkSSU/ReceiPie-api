package com.parkssu.receipie_api.ocr.domain;

import lombok.*;

/**
 * Item은 영수증에 포함된 개별 품목을 나타냅니다.
 * 하나의 Receipt는 여러 개의 Item으로 구성됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String name;    // 품명 (예: 콜라)
    private int count;      // 수량
    private int price;      // 가격
}
