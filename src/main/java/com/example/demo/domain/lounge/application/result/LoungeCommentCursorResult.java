package com.example.demo.domain.lounge.application.result;

import java.util.List;

public record LoungeCommentCursorResult(
    List<LoungeCommentListResult> comments, Long nextCursorId, int size, boolean hasNext) {}
