package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingResponse(
    Long personalFeelingId, Long userId, String content, LocalDateTime createdAt) {}
