package com.example.demo.domain.archive.presentation.response;

import java.time.LocalDateTime;

public record ArchiveWorkResponse(
    Long archiveWorkId, Long displayArtworkId, Long userId, LocalDateTime savedAt) {}
