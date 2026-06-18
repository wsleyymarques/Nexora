package com.nexora.security;

import com.nexora.model.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Getter
@Setter
@Component
@RequestScope
public class CurrentRequest {

    private String ip;

    private String origin;

    private String referer;

    private String userAgent;

    private String path;

    private String method;

    private User user;

    private UUID userId;

    private UUID registeredClientId;
}
