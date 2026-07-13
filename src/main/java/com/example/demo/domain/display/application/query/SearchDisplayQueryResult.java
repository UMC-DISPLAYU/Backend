package com.example.demo.domain.display.application.query;

import java.time.LocalDate;

public record SearchDisplayQueryResult(
    Long displayId, String title, String posterImageUrl, LocalDate startedAt, LocalDate endedAt) {}
