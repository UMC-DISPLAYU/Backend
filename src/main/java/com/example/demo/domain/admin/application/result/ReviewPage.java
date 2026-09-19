package com.example.demo.domain.admin.application.result;

import java.util.List;

public record ReviewPage(List<ReviewSummary> items, Long nextCursor, boolean hasNext) {}
