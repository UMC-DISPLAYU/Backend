package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ReviewHistory(
    Long screeningId,
    String status,
    Instant requestedAt,
    Instant processedAt,
    Long reviewerId,
    String rejectionReason) {}
