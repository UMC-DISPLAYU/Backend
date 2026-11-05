package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;

public record UserStatusResponse(Long userId, String accountStatus, Instant processedAt) {}
