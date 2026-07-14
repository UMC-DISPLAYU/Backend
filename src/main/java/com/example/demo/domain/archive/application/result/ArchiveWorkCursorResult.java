package com.example.demo.domain.archive.application.result;

import java.util.List;

public record ArchiveWorkCursorResult(
    List<ArchiveWorkResult> works, Long nextCursorId, int size, boolean hasNext) {}
