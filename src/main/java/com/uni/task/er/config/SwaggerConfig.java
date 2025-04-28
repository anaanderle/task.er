package com.uni.task.er.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("task.er")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info()
                        .title("Task.er")
                        .description("Documentação do Task.er")
                        .version("1.0.0")
                        ))
                .build();
    }
}
