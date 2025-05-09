package com.parkssu.receipie_api.ocr.controller;

import com.parkssu.receipie_api.ocr.dto.ImageParsingRequest;
import com.parkssu.receipie_api.ocr.dto.ReceiptResponseDto;
import com.parkssu.receipie_api.ocr.service.OCRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OCRController는 클라이언트 요청을 받아 OCR 분석을 수행하고,
 * 결과를 JSON 형태로 응답하는 REST API 컨트롤러입니다.
 */
@RestController // Spring의 REST 컨트롤러임을 명시 (자동으로 JSON 반환)
@RequestMapping("/ocr") // 모든 경로는 /ocr 하위로 시작됨
@RequiredArgsConstructor // 생성자 주입 자동 생성
@Tag(name="OCR", description = "OCR 관련 API")
public class OCRController {

    private final OCRService ocrService;

    /**
     * POST /ocr/analyze
     * base64 이미지 데이터를 분석하여 영수증 정보를 반환합니다.
     */
    @PostMapping("/analyze")
    @Operation(
            summary = "영수증 이미지 분석 API",
            description = """
                여러 장의 Base64 인코딩된 이미지를 받아 OCR 추출 후, GPT를 통해 항목별 영수증 정보를 반환합니다.
                - 긴 영수증을 여러 장 촬영한 경우에도 순서대로 이어붙여 분석합니다.
                - 응답은 품목 리스트, 총합계, 상호명, 날짜 정보를 포함합니다.
                """
    )
    public ResponseEntity<ReceiptResponseDto> analyze(
            @RequestBody ImageParsingRequest request) throws Exception {
        ReceiptResponseDto result = ocrService.processImages(request.getBase64Images());
        return ResponseEntity.ok(result);
    }

}
