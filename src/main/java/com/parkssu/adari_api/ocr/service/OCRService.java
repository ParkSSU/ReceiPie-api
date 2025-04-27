package com.parkssu.adari_api.ocr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkssu.adari_api.external.clova.ClovaOCRClient;
import com.parkssu.adari_api.external.gpt.GPTClient;
import com.parkssu.adari_api.ocr.domain.Item;
import com.parkssu.adari_api.ocr.domain.Receipt;
import com.parkssu.adari_api.ocr.dto.ReceiptResponseDto;
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
@RequiredArgsConstructor  // 생성자 주입 자동 생성 (final 필드에 대한 생성자)
public class OCRService {

    private final ClovaOCRClient clovaClient;
    private final GPTClient gptClient;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파서

    @Value("${receipt.storage-path}")  // application.yml에서 주입 test 저장할 경로
    private String receiptFolder;

    /**
     * base64 이미지를 받아서 영수증 응답 DTO로 가공합니다.
     */
    public ReceiptResponseDto processImage(String base64) throws IOException {
        // 1. OCR 수행 (Clova)
        String extractedText = clovaClient.extractText(base64);

        // 2. GPT 프롬프트 생성
        String prompt = buildPrompt(extractedText);

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
        설명은 절대 하지 말고, JSON 형식만 출력해주세요.

        {
          "items": [
            { "name": "콜라", "count": 2, "price": 3000 },
            { "name": "삼각김밥", "count": 1, "price": 1200 }
          ],
          "total_price": 4200,
          "store": "이마트24",
          "date": "2024-04-21"
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
        Path folder = Paths.get(receiptFolder).toAbsolutePath(); // 상대경로 → 절대경로 변환
        Files.createDirectories(folder); // 폴더 없으면 생성

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
