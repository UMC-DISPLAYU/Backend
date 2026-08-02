package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import java.time.LocalDateTime;

public record LoungePostQueryResult(
    Long cursorId,
    Long loungePostId,
    Long authorUserId,
    String title,
    String content,
    LoungePostCategory category,
    LocalDateTime createdAt) {}
