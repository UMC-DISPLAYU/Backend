package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DisplayReviewReplyLikeResult(
    Long displayReviewReplyId,
    Boolean liked,
    Integer likeCount,
    LocalDateTime createdAt,
    LocalDateTime deletedAt) {}
