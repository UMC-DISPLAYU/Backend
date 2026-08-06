package com.example.demo.domain.archive.domain.repository;

import java.time.LocalDate;

public record DisplaySummary(
    Long displayId,
    String title,
    String organization,
    String department,
    String placeName,
    LocalDate startDate,
    LocalDate endDate,
    String posterImageUrl) {}
