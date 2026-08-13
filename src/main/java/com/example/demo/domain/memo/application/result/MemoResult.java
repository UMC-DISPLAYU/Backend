package com.example.demo.domain.memo.application.result;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemoResult(
    Long memoId,
    Long archiveDisplayId,
    Long archiveWorkId,
    Long archivePersonalWorkId,
    String content,
    LocalDate visitDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
