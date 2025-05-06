package com.parkssu.receipie_api.global.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Adari API 문서")
                        .description("Adari 프로젝트의 영수증 OCR API 명세서입니다.")
                        .version("v1.0.0"));
    }
}