package com.parkssu.adari_api.ocr;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.parkssu.adari_api.gpt.dto.ChatGPTRequest;
import com.parkssu.adari_api.gpt.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/ocr")
public class OCRController {

    @Autowired
    private ClovaOCRService clovaService;

    @Autowired
    private RestTemplate template;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String gptUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/analyze")
    public Map<String, Object> analyzeReceipt(@RequestBody ImageParsingRequest request) throws Exception {
        // 1. Clova OCR로부터 텍스트 추출
        String ocrText = clovaService.execute(request);

        // 2. 프롬프트 생성
        String prompt = """
                다음은 영수증 내용입니다. 항목별로 품명(name), 개수(count), 가격(price), 총합계(total_price), 상호명(store), 날짜(date)를 아래 JSON 예시 형식에 맞춰 추출해주세요.

                설명은 절대 하지 말고, JSON 형식만 출력해주세요.

                예시 답변 형식:
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

        // 3. GPT 요청 전송
        ChatGPTRequest gptRequest = new ChatGPTRequest(model, prompt);
        ChatGPTResponse gptResponse = template.postForObject(gptUrl, gptRequest, ChatGPTResponse.class);

        String rawResponse = gptResponse.getChoices().get(0).getMessage().getContent();

        // 4. JSON 블록만 추출
        String jsonOnly = extractJsonWithRegex(rawResponse);

        // 5. JSON 파싱하여 객체로 반환
        return objectMapper.readValue(jsonOnly, new TypeReference<Map<String, Object>>() {
        });
    }

    // 정규식 기반 JSON 블록 추출 유틸
    private String extractJsonWithRegex(String text) {
        Pattern pattern = Pattern.compile("\\{(?:[^{}]|\\{[^{}]*\\})*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("JSON 블록을 찾을 수 없습니다.");
    }
}

