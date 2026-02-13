package com.example.pointroulette.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

  @Bean
  fun openAPI(): OpenAPI {
    return OpenAPI()
      .info(
        Info()
          .title("Point Roulette API")
          .description("포인트 룰렛 서비스 API 문서")
          .version("0.1.0")
      )
      .components(
        Components()
          .addSecuritySchemes(
            "bearerAuth",
            SecurityScheme()
              .type(SecurityScheme.Type.HTTP)
              .scheme("bearer")
              .bearerFormat("JWT")
              .`in`(SecurityScheme.In.HEADER)
              .name("Authorization")
          )
      )
  }
}
