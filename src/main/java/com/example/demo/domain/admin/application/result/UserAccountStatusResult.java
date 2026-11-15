package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record UserAccountStatusResult(
    Long userId, String accountStatus, Instant suspendedAt, Long operatorId, String reason) {}
