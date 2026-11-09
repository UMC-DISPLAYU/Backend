package com.example.demo.domain.admin.application.result;

import java.util.List;

public record ContentPage(List<ContentSummary> items, String nextCursor, boolean hasNext) {}
