package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/** 심사 미리보기 초안. 공개 제한을 우회하는 일반 사용자 조회에 사용하지 않는다. */
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
    List<String> imageUrls) {}
