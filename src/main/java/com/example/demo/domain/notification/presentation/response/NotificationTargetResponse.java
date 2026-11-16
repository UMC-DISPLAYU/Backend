package com.example.demo.domain.notification.presentation.response;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationTargetResponse(
    @Schema(
            description =
                "이동할 화면. DISPLAY_DETAIL: 전시 상세, DISPLAY_WORKS: 전시 작업, MY_DISPLAYS: 내 전시 관리,"
                    + " MY_INVITATIONS: 받은 초대",
            example = "DISPLAY_DETAIL")
        NotificationTargetType type,
    @Schema(description = "이동할 대상 ID(전시 ID). 화면만 가리키면 null", example = "7") Long id) {}
