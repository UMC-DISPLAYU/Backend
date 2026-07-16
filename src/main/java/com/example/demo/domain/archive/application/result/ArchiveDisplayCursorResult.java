package com.example.demo.domain.archive.application.result;

import java.util.List;

public record ArchiveDisplayCursorResult(
    List<ArchiveDisplayResult> displays, Long nextCursorId, int size, boolean hasNext) {}
