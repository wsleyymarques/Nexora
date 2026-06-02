package com.nexora.dto.response;
import java.util.UUID;
public record StoreResponse(UUID id, String name, String slug, String description, boolean active) {}
