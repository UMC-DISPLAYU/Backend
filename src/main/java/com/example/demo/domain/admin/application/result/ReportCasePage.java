package com.example.demo.domain.admin.application.result;

import java.util.List;

public record ReportCasePage(List<ReportCaseSummary> items, Long nextCursor, boolean hasNext) {}
