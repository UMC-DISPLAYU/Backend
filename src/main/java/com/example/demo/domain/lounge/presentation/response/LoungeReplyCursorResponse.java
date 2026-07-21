package com.example.demo.domain.lounge.presentation.response;

import java.util.List;

public record LoungeReplyCursorResponse(
    List<LoungeReplyListResponse> replies, Long nextCursorId, int size, boolean hasNext) {}
