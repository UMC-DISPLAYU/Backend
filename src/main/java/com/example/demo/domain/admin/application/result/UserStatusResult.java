package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record UserStatusResult(Long userId, String accountStatus, Instant processedAt) {}
