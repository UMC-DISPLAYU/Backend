package com.example.demo.domain.display.application.query;

import java.time.LocalDateTime;

public record DuPickQueryResult(
    Long duPickId,
    String title,
    String subtitle,
    String bannerImageUrl,
    String authorName,
    LocalDateTime createdAt) {}
