package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;

public record LoungeCommentListResponse(
    Long loungeCommentId,
    Long parentCommentId,
    String content,
    String commentStatus,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long likeCount,
    long replyCount,
    boolean isLiked,
    boolean isMyComment) {}
