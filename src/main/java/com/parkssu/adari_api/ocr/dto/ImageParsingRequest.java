package com.parkssu.adari_api.ocr.dto;

import lombok.*;

/**
 * 클라이언트가 서버로 이미지를 전송할 때 사용하는 요청 객체입니다.
 * 이미지 데이터를 base64 인코딩된 문자열 형태로 전달받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageParsingRequest {
    private String base64;  // 클라이언트에서 전달하는 base64 이미지 문자열
}
