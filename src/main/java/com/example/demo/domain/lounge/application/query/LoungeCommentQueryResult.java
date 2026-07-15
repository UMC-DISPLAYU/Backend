package com.example.demo.domain.lounge.application.query;

import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import java.time.LocalDateTime;

public record LoungeCommentQueryResult(
    Long loungeCommentId,
    Long loungePostId,
    Long parentCommentId,
    Long authorUserId,
    String content,
    LoungeCommentStatus commentStatus,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
