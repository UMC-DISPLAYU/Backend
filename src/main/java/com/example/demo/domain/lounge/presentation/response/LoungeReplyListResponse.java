package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungeReplyListResponse(
    Long loungeCommentId,
    Long parentCommentId,
    String content,
    List<String> imageUrls,
    String commentStatus,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long likeCount,
    boolean isLiked,
    boolean isMyComment) {}
