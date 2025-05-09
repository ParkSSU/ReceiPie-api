package com.parkssu.receipie_api.global.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "JWT Authentication",  // 인증 스키마 이름, OpenAPI 문서에서 참조할 이름
        type = SecuritySchemeType.HTTP, // 인증 타입: HTTP 기반 인증
        scheme = "bearer", // 인증 스키마: Bearer (JWT 토큰 방식)
        bearerFormat = "JWT" // 토큰 형식: JWT (JSON Web Token)
)
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("ReceiPie API")
                        .description("영수증 관리 및 정산을 위한 API 문서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }
}