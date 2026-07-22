package com.example.demo.domain.memo.application.command;

import java.time.LocalDate;

public record UpsertExhibitionMemoCommand(
    Long userId, Long archiveDisplayId, String content, LocalDate visitDate) {}
