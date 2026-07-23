package com.example.demo.domain.display.application.query;

import java.time.LocalDate;

public record ClosingSoonDisplayQueryResult(
    Long displayId,
    String title,
    String posterImageUrl,
    String organization,
    String department,
    LocalDate startedAt,
    LocalDate endedAt) {}
