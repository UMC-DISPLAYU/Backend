package com.example.demo.domain.display.application.result;

import java.util.List;

public record DisplayScreeningPageResult(
    List<DisplayScreeningSummaryResult> items, Long nextCursor, boolean hasNext) {}
