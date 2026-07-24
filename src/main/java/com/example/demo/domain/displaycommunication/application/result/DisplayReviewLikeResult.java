package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DisplayReviewLikeResult(
    Long displayReviewId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
