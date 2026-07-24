package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedDisplayReviewReplyResponse(
    Long displayReviewReplyId, LocalDateTime deletedAt) {}
