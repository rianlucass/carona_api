package com.rianlucas.carona_api.services;

import com.rianlucas.carona_api.infra.exceptions.file.FileUploadException;
import com.rianlucas.carona_api.infra.exceptions.file.FileSizeLimitExceededException;
import com.rianlucas.carona_api.infra.exceptions.file.InvalidFileTypeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // Extensões permitidas para imagens
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    // Tamanho máximo de arquivo 5mb
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public String uploadPhoto(MultipartFile file) {

        // Se o arquivo for null ou vazio, retorna null (foto não obrigatória)
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar tamanho do arquivo
        validateFileSize(file);

        // Validar tipo de arquivo
        validateFileType(file);

        try {
            // Criar diretório de upload se não existir
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String nameFile = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Salvar o arquivo
            Path filePath = uploadPath.resolve(nameFile);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return nameFile;

        } catch (IOException e) {
            throw new FileUploadException("Erro ao salvar o arquivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FileUploadException("Erro inesperado durante o upload do arquivo", e);
        }
    }

    /**
     * Valida se o tamanho do arquivo está dentro do limite permitido
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException(
                    String.format("O arquivo excede o tamanho máximo permitido de %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }

    /**
     * Valida se a extensão do arquivo é permitida
     */
    private void validateFileType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new InvalidFileTypeException("Nome do arquivo inválido");
        }

        String fileExtension = getFileExtension(originalFilename);
        
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new InvalidFileTypeException(
                    String.format("Tipo de arquivo não permitido. Extensões aceitas: %s", String.join(", ", ALLOWED_EXTENSIONS))
            );
        }
    }

    /**
     * Extrai a extensão do arquivo
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
