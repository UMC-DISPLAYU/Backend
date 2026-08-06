package com.example.demo.domain.displaycommunication.application.query;

public record GetDisplayReviewsQuery(Long displayId, Long cursorId, int size, Long viewerUserId) {}
