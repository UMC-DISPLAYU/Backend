package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DeletedDisplayReviewReplyResult(Long displayReviewReplyId, LocalDateTime deletedAt) {}
