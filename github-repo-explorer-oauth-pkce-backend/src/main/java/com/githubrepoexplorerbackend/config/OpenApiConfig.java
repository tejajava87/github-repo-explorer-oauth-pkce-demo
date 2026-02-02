package com.githubrepoexplorerbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Provides OpenAPI metadata used by springdoc to generate the OpenAPI JSON and
     * power the Swagger UI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("GitHub Repo Explorer Backend API")
                        .version("v1")
                        .description("Backend API for GitHub Repo Explorer demo (OAuth PKCE)"));
    }
}
