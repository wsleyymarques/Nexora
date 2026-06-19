package com.nexora.service;

import com.nexora.exception.InvalidFileException;
import com.nexora.integration.storage.ImageFolder;
import com.nexora.integration.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final StorageProvider storageProvider;

    public String upload(MultipartFile file, ImageFolder folder) {
        validate(file);

        String extension = resolveExtension(file.getContentType());
        String key = folder.getPath() + "/" + UUID.randomUUID() + "." + extension;

        try {
            return storageProvider.upload(
                    key,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );
        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar arquivo para upload", e);
        }
    }

    public String uploadFromUrl(String sourceUrl, ImageFolder folder) {
        if (sourceUrl == null || sourceUrl.isBlank()) {
            return null;
        }

        try {
            java.net.URL url = new java.net.URL(sourceUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String contentType = connection.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                contentType = "image/jpeg";
            }

            String extension = resolveExtension(contentType);
            String key = folder.getPath() + "/" + UUID.randomUUID() + "." + extension;

            try (java.io.InputStream inputStream = connection.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();

                if (bytes.length > MAX_SIZE_BYTES) {
                    return null;
                }

                return storageProvider.upload(
                        key,
                        new java.io.ByteArrayInputStream(bytes),
                        contentType,
                        bytes.length
                );
            }
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteByUrl(String publicUrl) {
        if (publicUrl == null) {
            return;
        }
        String key = storageProvider.extractKeyFromUrl(publicUrl);
        if (key != null) {
            storageProvider.delete(key);
        }
    }

    public void delete(String key) {
        storageProvider.delete(key);
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Arquivo nao pode ser vazio");
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidFileException("Arquivo excede o tamanho maximo de 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileException("Tipo de arquivo nao permitido. Use JPEG, PNG ou WEBP");
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new InvalidFileException("Tipo de arquivo nao suportado");
        };
    }
}