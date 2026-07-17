package com.example.demo.global.file.presentation.request;

import com.example.demo.global.file.application.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePresignedUrlRequest(
    @Schema(
            description = "파일 종류",
            example = "IMAGE",
            allowableValues = {"IMAGE", "VIDEO"})
        @NotNull FileType fileType,
    @Schema(description = "파일을 사용하는 기능 영역", example = "display") @NotBlank String domain,
    @Schema(description = "원본 파일명", example = "poster.png") @NotBlank String fileName,
    @Schema(
            description =
                "업로드할 파일 Content-Type. IMAGE: image/jpeg, image/png, image/webp, image/gif / VIDEO: video/mp4, video/quicktime, video/webm",
            example = "image/png",
            allowableValues = {
              "image/jpeg",
              "image/png",
              "image/webp",
              "image/gif",
              "video/mp4",
              "video/quicktime",
              "video/webm"
            })
        @NotBlank String contentType,
    @Schema(description = "파일 크기(byte)", example = "1048576") @Positive long fileSize) {}
