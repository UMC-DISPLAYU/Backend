package com.example.demo.domain.archive.application.result;

import java.time.LocalDateTime;

public record ArchiveArtistResult(
    Long archiveArtistId, Long creatorId, Long userId, LocalDateTime savedAt) {}
