package com.muftisaleh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    OpenAPI muftiSalehOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mufti Saleh API")
                        .version("v1")
                        .description("""
                                Backend API for Shaykh Abu Saleh Muhammadullah's website.

                                Public endpoints (announcements, Q&A browse, schedule, media, masail) \
                                require no authentication. Question submission and bay'ah requests are \
                                also open to the public.

                                Admin endpoints under /api/v1/admin/* require a bearer JWT obtained \
                                from POST /api/v1/auth/login.""")
                        .contact(new Contact().name("Mufti Saleh Web Team")))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Admin access token")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER));
    }
}
