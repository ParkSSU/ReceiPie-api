package com.parkssu.adari_api.ocr.dto;

import lombok.*;
import java.util.List;

/**
 * 클라이언트에게 반환되는 응답 DTO입니다.
 * 내부 도메인 객체인 Receipt을 응답 전용 구조로 변환한 형태입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptResponseDto {

    private List<ItemDto> items;
    private int totalPrice;
    private String store;
    private String date;

    /**
     * 응답 전용 품목 DTO입니다.
     * 외부에 반환되는 형태이므로 내부 Item 도메인 객체와 분리합니다.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private String name;
        private int count;
        private int price;
    }
}
