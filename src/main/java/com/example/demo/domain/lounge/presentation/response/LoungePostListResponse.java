package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungePostListResponse(
    Long loungePostId,
    String category,
    String title,
    String content,
    List<String> postImageUrls,
    LoungeWriterResponse writer,
    LocalDateTime createdAt,
    long commentCount,
    long likeCount,
    boolean isLiked,
    boolean isMyPost) {}
