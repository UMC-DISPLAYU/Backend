package com.example.demo.domain.displaycommunication.presentation.response;

import java.time.LocalDateTime;

public record DisplayReviewReplyResponse(
    Long displayReviewReplyId,
    LocalDateTime createdAt,
    String content,
    Long displayReviewId,
    Long userId,
    String nickname,
    boolean isTeamMember) {}
