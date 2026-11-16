package com.example.demo.domain.notification.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record NotificationCursorResponse(
    @Schema(description = "알림 목록 (최신순)") List<NotificationResponse> notifications,
    @Schema(description = "다음 페이지 조회용 커서 (notificationId). 마지막 페이지면 null", example = "30")
        Long nextCursorId,
    @Schema(description = "페이지 크기", example = "10") int size,
    @Schema(description = "다음 페이지 존재 여부", example = "true") boolean hasNext) {}
