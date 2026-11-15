package com.example.demo.domain.display.presentation.request;

import com.example.demo.global.file.domain.MediaType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "DisplayMediaRequest")
public record DisplayMediaRequest(
    @NotBlank @Schema(description = "업로드된 원본 URL. VIDEO는 전시용 업로드 경로여야 합니다.") String imageUrl,
    @NotNull @Schema(description = "IMAGE 또는 VIDEO. 생략할 수 없습니다.") MediaType mediaType,
    @Schema(
            description =
                "VIDEO는 업로드된 이미지 썸네일 URL 필수. IMAGE는 null 또는 생략. 영상은 서버에서 200MB 이하를 확인하며 30초 이하 길이는 프론트에서 검증합니다.")
        String thumbnailUrl) {}
