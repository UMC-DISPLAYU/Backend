package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkFeelingResult(
    Long feelingId, Long userId, String content, LocalDateTime createdAt) {}
