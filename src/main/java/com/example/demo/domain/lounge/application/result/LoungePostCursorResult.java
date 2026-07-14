package com.example.demo.domain.lounge.application.result;

import java.util.List;

public record LoungePostCursorResult(
    List<LoungePostListResult> posts, Long nextCursorId, int size, boolean hasNext) {}
