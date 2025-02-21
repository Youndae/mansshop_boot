package com.example.mansshop_boot.config;

import com.example.mansshop_boot.config.customException.ExceptionEntity;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@OpenAPIDefinition(info = @Info(title = "Man's Shop Application API",
                                version = "1.4.0",
                                description = "Man's Shop API Documents"))
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addParameters("Authorization",
                                new Parameter()
                                        .in(ParameterIn.HEADER.toString())
                                        .required(true)
                                        .name("Authorization")
                                        .description("Authorization AccessToken Header")
                                        .schema(new StringSchema())
                        )
                        .addParameters("Authorization_Refresh",
                                new Parameter()
                                        .in(ParameterIn.COOKIE.toString())
                                        .required(true)
                                        .name("Authorization_Refresh")
                                        .description("Authorization RefreshToken Cookie")
                                        .schema(new StringSchema())
                        )
                        .addParameters("Authorization_ino",
                                new Parameter()
                                        .in(ParameterIn.HEADER.toString())
                                        .required(true)
                                        .name("Authorization_ino")
                                        .description("Authorization ino Cookie. Identifier Value")
                                        .schema(new StringSchema())
                        )
                );
    }

}
