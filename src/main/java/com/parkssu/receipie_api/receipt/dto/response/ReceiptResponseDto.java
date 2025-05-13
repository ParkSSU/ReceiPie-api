package com.parkssu.receipie_api.receipt.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ ReceiptResponseDto - 영수증 조회 시 응답하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResponseDto {

    private Long id;
    private String storeName;
    private LocalDateTime date;
    private int totalPrice;
    private List<ItemResponseDto> items;
    private List<ParticipantResponseDto> participants;

}
