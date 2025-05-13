package com.parkssu.receipie_api.ocr.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 클라이언트가 서버로 이미지를 전송할 때 사용하는 요청 객체입니다.
 * 이미지 데이터를 base64 인코딩된 문자열 형태로 전달받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageParsingRequest {
    @Schema(
            description = "Base64 인코딩된 이미지들의 리스트. 긴 영수증을 여러 장 촬영한 경우 여러 이미지를 업로드합니다.",
            example = "[\"data:image/jpeg;base64,/9j/4AAQSkZ...\", \"data:image/jpeg;base64,/9j/4BBQSkZ...\"]"
    )
    private List<String> base64Images;

    /**
     * 이미지 개수를 반환하는 메서드
     *
     * @return int - 이미지 개수
     */
    public int getImageCount() {
        return base64Images == null ? 0 : base64Images.size();
    }
}
