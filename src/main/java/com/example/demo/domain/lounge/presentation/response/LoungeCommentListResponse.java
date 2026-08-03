package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungeCommentListResponse(
    Long loungeCommentId,
    Long parentCommentId,
    String content,
    List<String> imageUrls,
    String commentStatus,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long likeCount,
    long replyCount,
    boolean isLiked,
    boolean isMyComment) {}
