package com.nexora.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class StorageConfig {

    @Value("${nexora.storage.endpoint}")
    private String endpoint;

    @Value("${nexora.storage.access-key}")
    private String accessKey;

    @Value("${nexora.storage.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("auto")) // R2 usa "auto"
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }
}