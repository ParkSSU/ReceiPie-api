package com.parkssu.receipie_api.receipt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BuyerDto - 구매자 데이터 구조
 */
@Getter
@Setter
@NoArgsConstructor
public class BuyerDto {
    private String username;
    private int count;
}

