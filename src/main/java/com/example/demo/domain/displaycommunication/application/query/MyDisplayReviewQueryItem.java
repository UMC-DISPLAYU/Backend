package com.example.demo.domain.displaycommunication.application.query;

import java.time.LocalDateTime;

public record MyDisplayReviewQueryItem(
    Long displayReviewId,
    Long displayId,
    String displayName,
    String content,
    LocalDateTime createdAt) {}
