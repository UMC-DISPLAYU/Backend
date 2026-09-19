package com.example.demo.domain.admin.application.result;

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
    String leaderName) {}
