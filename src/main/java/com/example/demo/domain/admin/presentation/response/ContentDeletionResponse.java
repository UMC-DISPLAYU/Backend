package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;

public record ContentDeletionResponse(
    String deletedTargetType, Long deletedTargetId, Instant processedAt) {}
