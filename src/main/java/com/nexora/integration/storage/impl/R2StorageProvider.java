package com.nexora.integration.storage.impl;

import com.nexora.integration.storage.StorageProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class R2StorageProvider implements StorageProvider {

    private final S3Client s3Client;

    @Value("${nexora.storage.bucket}")
    private String bucket;

    @Value("${nexora.storage.public-base-url}")
    private String publicBaseUrl;

    @Override
    public String upload(String key, InputStream content, String contentType, long contentLength) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(content, contentLength)
        );
        return getPublicUrl(key);
    }

    @Override
    public void delete(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }

    @Override
    public String getPublicUrl(String key) {
        return publicBaseUrl + "/" + key;
    }

    @Override
    public String extractKeyFromUrl(String publicUrl) {
        if (publicUrl == null || !publicUrl.startsWith(publicBaseUrl)) {
            return null;
        }
        return publicUrl.substring(publicBaseUrl.length() + 1);
    }
}