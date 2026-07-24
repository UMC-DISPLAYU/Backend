package com.example.demo.domain.displaycommunication.application.command;

public record DeleteDisplayReviewReplyCommand(
    Long displayId, Long displayReviewId, Long displayReviewReplyId, Long userId) {}
