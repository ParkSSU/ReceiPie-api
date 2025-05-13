package com.parkssu.receipie_api.receipt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ ReceiptRequestDto - 영수증 생성 요청 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ReceiptRequestDto {
    private String storeName;
    private LocalDateTime date;
    private int totalPrice;
    private List<String> participants;
    private List<ItemRequestDto> items;
}
