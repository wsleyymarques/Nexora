package com.nexora.integration.storage;

import lombok.Getter;

@Getter
public enum ImageFolder {

    PROFILE_PHOTOS("profile-photos"),
    PRODUCTS("products"),
    STORES("stores");

    private final String path;

    ImageFolder(String path) {
        this.path = path;
    }
}