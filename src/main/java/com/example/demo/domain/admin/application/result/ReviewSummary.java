package com.example.demo.domain.admin.application.result;

import java.time.Instant;

public record ReviewSummary(
    Long displayId, String title, Long requesterId, String status, Instant requestedAt) {}
