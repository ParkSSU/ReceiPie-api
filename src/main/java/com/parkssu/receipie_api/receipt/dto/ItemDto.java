package com.parkssu.receipie_api.receipt.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * ItemDto - 항목 데이터 구조
 */
@Getter
@Setter
@NoArgsConstructor
public class ItemDto {
    private String name;
    private int count;
    private int price;
    private List<BuyerDto> buyers;
}
