package com.example.demo.domain.display.application.query;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DisplayMapQueryResult(
    Long displayId,
    String title,
    LocalDate startDate,
    LocalDate endDate,
    String locationName,
    String posterImageUrl,
    String organization,
    String department,
    BigDecimal latitude,
    BigDecimal longitude) {}
