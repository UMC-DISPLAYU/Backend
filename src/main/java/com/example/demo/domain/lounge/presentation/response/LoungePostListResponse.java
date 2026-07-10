package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;

public record LoungePostListResponse(
        Long loungePostId,
        Long authorUserId,
        String title,
        String postImageUrl,
        String category,
        long likeCount,
        long commentCount,
        long scrapCount,
        LocalDateTime createdAt) {}