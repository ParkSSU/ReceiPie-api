package com.parkssu.receipie_api.receipt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReceiptRequestDto - 영수증 생성 요청 시 필요한 데이터 구조
 */
@Getter
@Setter
@NoArgsConstructor
public class ReceiptRequestDto {
    private String storeName;
    private LocalDateTime date;
    private int totalPrice;
    private List<String> participants;
    private List<ItemDto> items;
}

