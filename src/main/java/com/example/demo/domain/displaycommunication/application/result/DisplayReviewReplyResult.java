package com.example.demo.domain.displaycommunication.application.result;

import java.time.LocalDateTime;

public record DisplayReviewReplyResult(
    Long displayReviewReplyId,
    LocalDateTime createdAt,
    String content,
    Long displayReviewId,
    Long userId,
    String nickname,
    boolean isTeamMember) {}
