package com.example.demo.domain.archive.application.result;

import java.util.List;

public record ArchiveArtistCursorResult(
    List<ArchiveArtistResult> artists, Long nextCursorId, int size, boolean hasNext) {}
