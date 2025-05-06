package com.vietbevis.authentication.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi(@Value("${open-api.api-docs.group}") String apiDocs) {
    return GroupedOpenApi.builder()
        .group(apiDocs)
        .packagesToScan("com.vietbevis.authentication.controller")
        .build();
  }

  @Bean
  public OpenAPI openAPI(
      @Value("${open-api.api-docs.title}") String title,
      @Value("${open-api.api-docs.description}") String description,
      @Value("${open-api.api-docs.contact.email}") String email,
      @Value("${open-api.api-docs.contact.name}") String name,
      @Value("${open-api.api-docs.version}") String version,
      @Value("${open-api.api-docs.server}") String serverUrl,
      @Value("${open-api.api-docs.license.name}") String licenseName,
      @Value("${open-api.api-docs.license.url}") String licenseUrl
  ) {
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .servers(List.of(new Server().url(serverUrl)))
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
        .info(new Info()
            .title(title)
            .description(description)
            .version(version)
            .contact(new Contact().name(name).email(email))
            .license(new License().name(licenseName).url(licenseUrl)));
  }

}
