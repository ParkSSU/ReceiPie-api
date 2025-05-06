package com.parkssu.receipie_api.ocr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.receipie_api.external.clova.ClovaOCRClient;
import com.parkssu.receipie_api.external.gpt.GPTClient;
import com.parkssu.receipie_api.ocr.domain.Receipt;
import com.parkssu.receipie_api.ocr.dto.ReceiptResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * OCRService는 ClovaOCR → GPT → JSON 파싱 → 파일 저장 → DTO 변환
 * 전체 비즈니스 로직을 연결하는 핵심 서비스 계층입니다.
 */
@Service
@RequiredArgsConstructor
public class OCRService {

    private final ClovaOCRClient clovaClient;
    private final GPTClient gptClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파서

    @Value("${receipt.storage-path}") // application.yml에서 저장 경로 주입
    private String receiptFolder;

    /**
     * 여러 base64 이미지를 받아서, OCR → 텍스트 합치기 → GPT → 응답 DTO 반환
     */
    public ReceiptResponseDto processImages(List<String> base64Images) throws IOException {
        // 1. 여러 이미지에 대해 OCR 수행하고 결과 이어붙이기
        StringBuilder combinedText = new StringBuilder();
        for (String base64 : base64Images) {
            String extractedText = clovaClient.extractText(base64);
            combinedText.append(extractedText).append(" "); // 공백 구분으로 합치기
        }

        // 2. GPT 프롬프트 생성
        String prompt = buildPrompt(combinedText.toString());

        // 3. GPT 응답 받기
        String gptResult = gptClient.getStructuredJson(prompt);

        // 4. JSON 블록만 추출
        String json = extractJsonFromText(gptResult);

        // 5. JSON 파일 저장
        saveToFile(json);

        // 6. JSON → Receipt 객체 변환
        Receipt receipt = objectMapper.readValue(json, Receipt.class);

        // 7. Receipt → DTO 변환 후 반환
        return convertToDto(receipt);
    }

    /**
     * GPT 프롬프트 문자열 생성
     */
    private String buildPrompt(String ocrText) {
        return """
                다음은 영수증 내용입니다. 항목별로 품명(name), 개수(count), 가격(price), 총합계(total_price), 상호명(store), 날짜(date)를 아래 JSON 예시 형식에 맞춰 추출해주세요.
                    
                - 설명은 절대 하지 말고 JSON 형식만 출력하세요.
                - 날짜(date)는 "YYYY-MM-DDTHH:MM:SS" 형태로 출력하세요. (T로 날짜와 시간을 구분)
                - 만약 추출할 수 없는 항목이 있다면 빈 문자열("") 또는 0으로 설정하세요.
                - 할인 항목은 무시하고, 실제 결제된 품목과 가격만 추출하세요.

                JSON 예시:
                {
                  "items": [
                    { "name": "콜라", "count": 2, "price": 3000 },
                    { "name": "삼각김밥", "count": 1, "price": 1200 }
                  ],
                  "total_price": 4200,
                  "store": "이마트24",
                  "date": "2024-04-21T14:35:42"
                }

                영수증 내용:
                """ + ocrText;
    }

    /**
     * GPT 응답에서 JSON 블록만 추출 (정규식 기반)
     */
    private String extractJsonFromText(String text) {
        Pattern pattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group();
        throw new IllegalArgumentException("유효한 JSON 블록을 찾을 수 없습니다.");
    }

    /**
     * 응답 JSON을 로컬 파일로 저장 (디버깅 및 기록용)
     */
    private void saveToFile(String json) throws IOException {
        Path folder = Paths.get(receiptFolder).toAbsolutePath();
        Files.createDirectories(folder);

        int index = 1;
        Path file;
        do {
            file = folder.resolve("receipt_" + index++ + ".json");
        } while (Files.exists(file));

        Files.write(file, json.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Receipt → DTO 변환 (도메인 객체 → 클라이언트 응답 구조)
     */
    private ReceiptResponseDto convertToDto(Receipt receipt) {
        List<ReceiptResponseDto.ItemDto> items = receipt.getItems().stream()
                .map(i -> new ReceiptResponseDto.ItemDto(i.getName(), i.getCount(), i.getPrice()))
                .collect(Collectors.toList());

        return new ReceiptResponseDto(items, receipt.getTotalPrice(), receipt.getStore(), receipt.getDate());
    }
}
