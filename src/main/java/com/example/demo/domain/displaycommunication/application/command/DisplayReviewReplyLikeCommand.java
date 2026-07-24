package com.example.demo.domain.displaycommunication.application.command;

public record DisplayReviewReplyLikeCommand(
    Long displayId, Long displayReviewId, Long displayReviewReplyId, Long userId) {}
