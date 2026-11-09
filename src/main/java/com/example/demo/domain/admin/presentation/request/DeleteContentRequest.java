package com.example.demo.domain.admin.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record DeleteContentRequest(
    @Schema(description = "삭제 사유. 앞뒤 공백 제거 후 1~1,000자", requiredMode = Schema.RequiredMode.REQUIRED)
        String reason) {}
