package com.example.demo.domain.admin.presentation.request;

import com.example.demo.domain.display.presentation.request.PosterCropRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

public record UpdateDuPickCropRequest(
    @Valid @Schema(description = "칼럼 배너의 원본 이미지 기준 비율. null 또는 생략하면 크롭을 해제합니다.")
        PosterCropRequest posterCrop) {}
