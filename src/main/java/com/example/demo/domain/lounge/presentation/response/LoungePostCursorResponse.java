package com.example.demo.domain.lounge.presentation.response;

import java.util.List;

public record LoungePostCursorResponse(
    List<LoungePostListResponse> posts, Long nextCursorId, int size, boolean hasNext) {}
