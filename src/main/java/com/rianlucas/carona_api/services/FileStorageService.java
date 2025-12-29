package com.rianlucas.carona_api.services;

import com.rianlucas.carona_api.domain.file.FileType;
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

    /**
     * Upload de foto com organização por tipo e ID do proprietário
     * Estrutura: uploads/{tipo}/{id}/{arquivo}
     * 
     * @param file Arquivo a ser enviado
     * @param fileType Tipo do arquivo (USER_PROFILE, VEHICLE_PHOTO, etc)
     * @param ownerId ID do proprietário (usuário, veículo, etc)
     * @return Caminho relativo do arquivo salvo
     */
    public String uploadPhoto(MultipartFile file, FileType fileType, String ownerId) {
        // Se o arquivo for null ou vazio, retorna null (foto não obrigatória)
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validar tamanho do arquivo
        validateFileSize(file);

        // Validar tipo de arquivo
        validateFileType(file);

        try {
            // Criar estrutura de diretórios: uploads/users/123/
            Path uploadPath = Paths.get(uploadDir, fileType.getDirectoryName(), ownerId);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Gerar nome único para o arquivo
            String timestamp = String.valueOf(System.currentTimeMillis());
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String nameFile = String.format("%s_%s_%s.%s", 
                UUID.randomUUID().toString(), 
                fileType.name().toLowerCase(), 
                timestamp,
                fileExtension
            );

            // Salvar o arquivo
            Path filePath = uploadPath.resolve(nameFile);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Retornar caminho relativo: users/123/arquivo.jpg
            return String.format("%s/%s/%s", fileType.getDirectoryName(), ownerId, nameFile);

        } catch (IOException e) {
            throw new FileUploadException("Erro ao salvar o arquivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FileUploadException("Erro inesperado durante o upload do arquivo", e);
        }
    }

    /**
     * Método legado mantido para compatibilidade
     * @deprecated Use uploadPhoto(MultipartFile, FileType, String) em vez disso
     */
    @Deprecated
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
     * Deleta um arquivo do sistema
     * @param filePath Caminho relativo do arquivo
     * @return true se deletado com sucesso, false caso contrário
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            Path path = Paths.get(uploadDir, filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                
                // Tentar deletar o diretório do usuário se estiver vazio
                Path parentDir = path.getParent();
                if (parentDir != null && Files.isDirectory(parentDir)) {
                    try {
                        if (isDirectoryEmpty(parentDir)) {
                            Files.delete(parentDir);
                        }
                    } catch (Exception e) {
                        // Ignora erro ao deletar diretório
                    }
                }
                
                return true;
            }
        } catch (IOException e) {
            throw new FileUploadException("Erro ao deletar o arquivo: " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Verifica se um diretório está vazio
     */
    private boolean isDirectoryEmpty(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            return false;
        }
        try (var entries = Files.list(directory)) {
            return !entries.findFirst().isPresent();
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
