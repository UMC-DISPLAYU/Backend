package com.example.demo.domain.display.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateDisplayContentCategoryRequest(
    @NotBlank String name,
    String description,
    @Schema(description = "섹션과 함께 생성할 사진 1~20장. 생략 또는 null이면 기존 빈 섹션 생성 방식 유지")
        @Size(min = 1, max = 20) List<@NotBlank String> imageUrls) {}
