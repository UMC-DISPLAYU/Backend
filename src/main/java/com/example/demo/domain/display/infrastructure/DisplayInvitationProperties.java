package com.example.demo.domain.display.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.display.invitation")
public record DisplayInvitationProperties(String baseUrl) {}
