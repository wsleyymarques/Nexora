package com.nexora.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record UserUpdateRequest(
        String name,
        String phone,
        @Email String email,
        @Size(min = 8) String newPassword,
        String currentPassword,
        MultipartFile avatarFile
) {}