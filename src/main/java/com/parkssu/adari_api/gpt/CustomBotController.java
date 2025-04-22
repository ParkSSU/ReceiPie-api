package com.parkssu.adari_api.gpt;

import com.parkssu.adari_api.gpt.dto.ChatGPTRequest;
import com.parkssu.adari_api.gpt.dto.ChatGPTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/bot")
public class CustomBotController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    // http://localhost:8080/bot/chat?prompt=안녕 GPT야
    @GetMapping("/chat")
    public String chat(@RequestParam(name = "prompt") String prompt) {
        // 1. 사용자 입력을 요청 객체로 포장
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);

        // 2. OpenAI 서버에 요청 전송
        ChatGPTResponse chatGPTResponse = template.postForObject(
                apiURL, request, ChatGPTResponse.class);

        // 3. 가장 첫 번째 응답 메시지를 추출하여 반환
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }
}
