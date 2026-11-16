package com.example.demo.domain.notification.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationBodySegmentResponse(
    @Schema(description = "내용 조각 문자열", example = "손끝의 지도") String text,
    @Schema(description = "굵게 강조할 조각인지 여부", example = "true") boolean bold) {}
