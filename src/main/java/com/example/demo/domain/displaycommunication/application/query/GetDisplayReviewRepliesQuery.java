package com.example.demo.domain.displaycommunication.application.query;

public record GetDisplayReviewRepliesQuery(
    Long displayId, Long displayReviewId, Long cursorId, int size) {}
