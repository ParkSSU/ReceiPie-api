package com.parkssu.receipie_api.receipt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ✅ ItemResponseDto - 항목 조회 시 응답하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

    private Long id;
    private String name;
    private int count;
    private int price;
    private List<BuyerResponseDto> buyers;
}
