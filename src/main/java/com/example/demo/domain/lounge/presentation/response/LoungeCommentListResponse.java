package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungeCommentListResponse(
    Long loungeCommentId,
    Long parentCommentId,
    Long authorUserId,
    String content,
    long likeCount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<LoungeCommentListResponse> replies) {}
