package com.rianlucas.carona_api.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileUrlBuilder {

    @Value("${app.base.url}")
    private String baseUrl;

    /**
     * Constrói a URL completa para acessar um arquivo de upload
     * 
     * @param relativePath Caminho relativo do arquivo (ex: "users/123/photo.jpg") ou URL completa
     * @return URL completa (ex: "http://localhost:8080/uploads/users/123/photo.jpg")
     */
    public String buildFileUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        // Se já for uma URL completa (http/https), retorna como está
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }
        
        // Caso contrário, constrói a URL local
        return baseUrl + "/uploads/" + relativePath;
    }
}
