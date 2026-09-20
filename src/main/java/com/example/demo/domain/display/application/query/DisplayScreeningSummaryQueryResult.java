package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.DisplayStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DisplayScreeningSummaryQueryResult(
    Long displayId,
    String title,
    Long requesterId,
    DisplayStatus status,
    LocalDateTime requestedAt,
    String school,
    String leaderName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {}
