package com.example.demo.domain.lounge.presentation.response;

import java.time.LocalDateTime;
import java.util.List;

public record LoungePostDetailResponse(
    Long loungePostId,
    String title,
    List<String> postImageUrls,
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
