package com.example.demo.domain.archive.application.result;

import java.time.LocalDateTime;

public record ArchiveWorkResult(
    Long archiveWorkId, Long displayArtworkId, Long userId, String memo, LocalDateTime savedAt) {}
