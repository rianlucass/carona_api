package com.rianlucas.carona_api.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Converter para caminho absoluto
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        String uploadLocation = "file:///" + uploadPath.toString().replace("\\", "/") + "/";
        
        // Permite acesso aos arquivos de upload via URL
        // Exemplo: http://192.168.1.104:8080/uploads/users/123/arquivo.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600); // Cache de 1 hora
    }
}
