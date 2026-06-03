package io.github.kmwh.tradeos_backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI().addServersItem(new Server().url("/")).info(
        new Info().title("TradeOS API 명세서").version("v1.0").description("TradeOS 백엔드 API 문서입니다."));
  }
}
