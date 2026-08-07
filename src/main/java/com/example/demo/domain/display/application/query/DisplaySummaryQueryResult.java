package com.example.demo.domain.display.application.query;

import java.time.LocalDate;

public record DisplaySummaryQueryResult(
    Long displayId,
    String title,
    String organization,
    String department,
    String placeName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {}
