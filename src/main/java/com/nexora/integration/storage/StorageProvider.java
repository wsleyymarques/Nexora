package com.nexora.integration.storage;

import java.io.InputStream;

public interface StorageProvider {

    String upload(String key, InputStream content, String contentType, long contentLength);

    void delete(String key);

    String getPublicUrl(String key);

    String extractKeyFromUrl(String publicUrl);
}