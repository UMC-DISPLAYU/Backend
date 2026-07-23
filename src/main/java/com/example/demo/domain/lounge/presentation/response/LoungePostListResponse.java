package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;

public record LoungePostListResponse(
    Long loungePostId,
    String category,
    String title,
    String content,
    String postImageUrl,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    long commentCount,
    long likeCount,
    boolean isLiked,
    boolean isMyPost) {}
