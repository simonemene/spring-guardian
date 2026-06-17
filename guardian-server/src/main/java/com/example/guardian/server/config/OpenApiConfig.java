package com.example.guardian.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI documentation exposed by the backend.
 *
 * @author p15518 - Simone Meneghetti
 */
@Configuration
public class OpenApiConfig {

    /**
     * Builds the OpenAPI metadata displayed by Swagger UI.
     *
     * @return OpenAPI metadata
     */
    @Bean
    public OpenAPI springGuardianOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Guardian API")
                        .version("1.0.0")
                        .description("REST API for scanning Spring projects and returning structured architecture review reports."));
    }
}
