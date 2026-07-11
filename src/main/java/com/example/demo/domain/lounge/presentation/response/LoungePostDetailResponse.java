package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;

public record LoungePostDetailResponse(
    Long loungePostId,
    Long authorUserId,
    String title,
    String postImageUrl,
    String content,
    String category,
    String status,
    long likeCount,
    long commentCount,
    long scrapCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
