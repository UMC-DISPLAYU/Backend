package com.example.demo.domain.memo.application.command;

import java.time.LocalDate;

public record UpsertPersonalWorkMemoCommand(
    Long userId, Long archivePersonalWorkId, String content, LocalDate visitDate) {}
