package com.parkssu.receipie_api.receipt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ BuyerRequestDto - 구매자 생성 시 사용하는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class BuyerRequestDto {
    private String username;
    private int count;
}
