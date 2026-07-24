package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DisplayReviewLikeResult(
    Long displayReviewId,
    boolean liked,
    int likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
