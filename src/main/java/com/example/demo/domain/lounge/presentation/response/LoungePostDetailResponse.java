package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;

public record LoungePostDetailResponse(
    Long loungePostId,
    String title,
    String postImageUrl,
    String content,
    String category,
    String postStatus,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long commentCount,
    long likeCount,
    boolean isLiked,
    boolean isScrapped,
    boolean isMyPost) {}
