package com.example.demo.domain.notification.application.result;

import java.util.List;

public record NotificationCursorResult(
    List<NotificationResult> notifications, Long nextCursorId, int size, boolean hasNext) {}
