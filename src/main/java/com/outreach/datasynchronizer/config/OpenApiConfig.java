package com.outreach.datasynchronizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI datasSynchronizerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Data Synchronizer API")
                        .description("REST API for User data synchronization — supports full CRUD operations")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Outreach Team")
                                .email("support@outreach.com")));
    }
}

