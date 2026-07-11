package com.example.demo.domain.archive.presentation.response;

import java.time.LocalDateTime;

public record ArchiveDisplayResponse(
    Long archiveDisplayId, Long displayId, Long userId, LocalDateTime savedAt) {}
