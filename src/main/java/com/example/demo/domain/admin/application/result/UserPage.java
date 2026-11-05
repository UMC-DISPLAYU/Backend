package com.example.demo.domain.admin.application.result;

import java.util.List;

public record UserPage(List<UserSummary> items, Long nextCursor, boolean hasNext) {}
