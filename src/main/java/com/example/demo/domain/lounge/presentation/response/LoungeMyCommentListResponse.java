package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungeMyCommentListResponse(
    Long loungeCommentId,
    Long loungePostId,
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
