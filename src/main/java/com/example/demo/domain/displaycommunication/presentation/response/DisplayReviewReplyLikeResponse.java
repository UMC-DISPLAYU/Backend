package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DisplayReviewReplyLikeResponse(
    Long displayReviewReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
