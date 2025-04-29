package com.github.memberboardspring.config.properties;


import com.github.memberboardspring.config.properties.server.ServerUrlProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerProperties {
    @Value("${springdoc.version}")
    private String openApiVersion;
    private final ServerUrlProperties serverUrlProperties;
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("access",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("Authorization"))
                        .addSecuritySchemes("refresh",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("RefreshToken"))
                        .addSecuritySchemes("cookie",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("RefreshToken"))
                )
                .info( createInfo(openApiVersion) )
                .servers( createServers() )
                .addSecurityItem(new SecurityRequirement().addList("access"))
                .addSecurityItem(new SecurityRequirement().addList("refresh"))
                .addSecurityItem(new SecurityRequirement().addList("cookie"));
    }

    private Info createInfo(String version) {
        return new Info()
                .title("Account Management Project")
                .version(version)
                .description("계정 관리 프로젝트");
    }

    protected List<Server> createServers() {
        Server baseServer = new Server()
                .url(serverUrlProperties.getBase())
                .description("HTTP Base Server");
        return List.of(baseServer);
    }

}
