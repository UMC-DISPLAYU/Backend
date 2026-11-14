package com.example.demo.domain.admin.application.result;

import com.example.demo.global.file.domain.MediaType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ReviewDetail(
    Long displayId,
    String title,
    String subtitle,
    String content,
    Long requesterId,
    String status,
    Instant requestedAt,
    Instant processedAt,
    Long reviewerId,
    String rejectionReason,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    List<String> imageUrls,
    String school,
    String leaderName,
    String screeningStatus,
    Long cancelledBy,
    List<MediaResult> media) {

  public record MediaResult(String imageUrl, MediaType mediaType, String thumbnailUrl) {}
}
