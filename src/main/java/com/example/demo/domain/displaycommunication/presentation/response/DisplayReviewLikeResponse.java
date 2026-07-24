package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DisplayReviewLikeResponse(
    Long displayReviewId,
    boolean liked,
    int likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
