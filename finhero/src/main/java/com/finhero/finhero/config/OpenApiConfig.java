package com.finhero.finhero.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI finheroOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Servidor de Desenvolvimento");

        Contact contact = new Contact();
        contact.setName("FinHero Team");
        contact.setEmail("suporte@finhero.com");

        Info info = new Info()
                .title("FinHero API")
                .version("1.0.0")
                .description("API REST para gerenciamento financeiro de duplas")
                .contact(contact);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}

