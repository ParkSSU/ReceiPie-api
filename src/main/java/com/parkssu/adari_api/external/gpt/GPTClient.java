package com.parkssu.adari_api.external.gpt;

import com.parkssu.adari_api.external.gpt.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * GPTClient는 OpenAI의 GPT API를 호출해 자연어 텍스트를 구조화된 JSON 형식으로 변환합니다.
 * Clova OCR로 추출한 텍스트를 기반으로 프롬프트를 생성해 전송합니다.
 */
@Component
@RequiredArgsConstructor
public class GPTClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api.url}")  // application.yml에 정의된 API URL
    private String gptUrl;

    @Value("${openai.model}")    // 사용할 모델 이름 (예: gpt-4o)
    private String model;

    /**
     * GPT에게 프롬프트를 전송하고 응답을 문자열 형태로 반환
     */
    public String getStructuredJson(String prompt) {
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse response = restTemplate.postForObject(
                gptUrl, request, ChatGPTResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
