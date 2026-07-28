package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DisplayReviewLikeResponse(
    Long displayReviewId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
