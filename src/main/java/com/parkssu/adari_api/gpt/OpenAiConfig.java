package com.parkssu.adari_api.gpt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;



@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openAiKey;

    // RestTemplate에 인터셉터를 추가해 Authorization 헤더 자동 추가
    @Bean
    public RestTemplate template() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openAiKey); // 인증 키 설정
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
