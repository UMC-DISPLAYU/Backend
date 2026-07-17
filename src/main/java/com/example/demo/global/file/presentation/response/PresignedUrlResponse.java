package com.example.demo.global.file.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(
    @Schema(description = "S3 업로드용 presigned PUT URL") String uploadUrl,
    @Schema(description = "S3 객체 key", example = "images/display/2026/07/uuid-poster.png")
        String fileKey,
    @Schema(
            description = "CloudFront 조회 URL",
            example =
                "https://d1tdgnysscm2va.cloudfront.net/images/display/2026/07/uuid-poster.png")
        String fileUrl,
    @Schema(description = "presigned URL 만료 시간(초)", example = "3600") long expiresIn) {}
