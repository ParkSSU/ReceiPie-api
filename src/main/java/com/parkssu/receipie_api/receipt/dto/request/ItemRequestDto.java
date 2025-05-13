package com.parkssu.receipie_api.receipt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * ✅ ItemRequestDto - 항목 생성 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemRequestDto {
    private String name;
    private int count;
    private int price;
    private List<BuyerRequestDto> buyers;
}
