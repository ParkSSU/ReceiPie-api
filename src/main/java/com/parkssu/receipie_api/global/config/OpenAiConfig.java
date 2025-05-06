package com.parkssu.receipie_api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * OpenAiConfig는 RestTemplate에 Authorization 헤더를 자동으로 주입해주는 설정 클래스입니다.
 */
@Configuration // 설정 클래스임을 나타냅니다.
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String apiKey;

    @Bean // 이 메서드가 반환하는 객체를 스프링 빈으로 등록
    public RestTemplate template() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
