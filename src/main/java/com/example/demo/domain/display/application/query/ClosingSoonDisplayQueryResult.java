package com.example.demo.domain.display.application.query;

import java.time.LocalDate;

public record ClosingSoonDisplayQueryResult(
    Long displayId, String title, String posterImageUrl, LocalDate startedAt, LocalDate endedAt) {}
