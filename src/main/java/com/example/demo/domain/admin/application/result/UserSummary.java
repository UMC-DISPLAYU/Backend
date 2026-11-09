package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record UserSummary(
    Long userId, String nickname, String email, String accountStatus, Instant createdAt) {}
