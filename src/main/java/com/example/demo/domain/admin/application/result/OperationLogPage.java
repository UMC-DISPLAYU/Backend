package com.example.demo.domain.admin.application.result;

import java.util.List;

public record OperationLogPage(List<OperationLog> items, Long nextCursor, boolean hasNext) {}
