package com.example.demo.domain.lounge.presentation.response;

import java.util.List;

public record LoungeReplyCursorResponse(
    List<LoungeCommentListResponse> replies, Long nextCursorId, int size, boolean hasNext) {}
