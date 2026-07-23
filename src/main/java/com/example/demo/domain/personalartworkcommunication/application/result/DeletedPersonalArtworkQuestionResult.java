package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkQuestionResult(
    Long personalQuestionId, LocalDateTime deletedAt) {}
