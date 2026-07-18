package com.example.demo.domain.memo.application.command;

import java.time.LocalDate;

public record UpsertArtworkMemoCommand(
    Long userId, Long archiveWorkId, String content, LocalDate visitDate) {}
