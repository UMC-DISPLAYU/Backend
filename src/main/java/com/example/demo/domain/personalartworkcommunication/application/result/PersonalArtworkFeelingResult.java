package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingResult(
    Long personalFeelingId, Long userId, String content, LocalDateTime createdAt) {}
