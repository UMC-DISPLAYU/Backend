package com.example.demo.domain.display.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateDisplayInvitationStatusRequest(
    @Schema(description = "초대 링크 활성화 여부. true면 새 초대 링크를 발급하고, false면 비활성화합니다.", example = "false")
        @NotNull Boolean enabled) {}
