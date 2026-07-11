package com.example.demo.domain.lounge.application.result;

import java.util.List;

public record LoungeReplyCursorResult(
    List<LoungeCommentListResult> replies, Long nextCursorId, int size, boolean hasNext) {}
