package com.example.demo.domain.displaycommunication.application.command;

public record CreateDisplayReviewReplyCommand(
    Long displayId, Long displayReviewId, Long userId, String content) {}
