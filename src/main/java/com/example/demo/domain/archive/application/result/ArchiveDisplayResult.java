package com.example.demo.domain.archive.application.result;

import java.time.LocalDateTime;

public record ArchiveDisplayResult(
    Long archiveDisplayId, Long displayId, Long userId, String memo, LocalDateTime savedAt) {}
