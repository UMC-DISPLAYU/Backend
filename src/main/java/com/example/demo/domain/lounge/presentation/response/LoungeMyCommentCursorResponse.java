package com.example.demo.domain.lounge.presentation.response;

import java.util.List;

public record LoungeMyCommentCursorResponse(
    List<LoungeMyCommentListResponse> comments, Long nextCursorId, int size, boolean hasNext) {}
