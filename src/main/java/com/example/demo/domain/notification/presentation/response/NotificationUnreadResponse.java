package com.example.demo.domain.notification.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationUnreadResponse(
    @Schema(description = "안 읽은 알림이 하나라도 있으면 true (마이 알림함 아이콘 빨간 점)", example = "true")
        boolean hasUnread) {}
