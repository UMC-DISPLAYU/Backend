package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.time.LocalDate;

public record ReviewSummary(
    Long displayId,
    String title,
    Long requesterId,
    String status,
    Instant requestedAt,
    String school,
    String leaderName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {}
