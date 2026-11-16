package com.example.demo.domain.notification.presentation.response;

import com.example.demo.domain.notification.domain.type.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationResponse(
    @Schema(description = "알림 ID", example = "31") Long notificationId,
    @Schema(description = "알림 종류", example = "EXHIBITION_MEMBER_REMOVED") NotificationType type,
    @Schema(description = "알림 제목", example = "전시에서 내보내짐") String title,
    @Schema(description = "알림 내용 조각 목록. 순서대로 이어 붙이고 bold가 true인 조각만 굵게 표시")
        List<NotificationBodySegmentResponse> body,
    @Schema(description = "읽음 여부. 알림함을 열기 전 기준", example = "false") boolean isRead,
    @Schema(description = "알림 생성 시각", example = "2026-11-16T09:00:00") LocalDateTime createdAt,
    @Schema(description = "누르면 이동할 곳. 이동하지 않는 알림이면 null") NotificationTargetResponse target) {}
