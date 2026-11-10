package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ContentDeletionResult(
    String deletedTargetType, Long deletedTargetId, Instant processedAt) {}
