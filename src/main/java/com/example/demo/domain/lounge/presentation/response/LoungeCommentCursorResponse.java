package com.example.demo.domain.lounge.presentation.response;

import java.util.List;

public record LoungeCommentCursorResponse(
    List<LoungeCommentListResponse> comments, Long nextCursorId, int size, boolean hasNext) {}
