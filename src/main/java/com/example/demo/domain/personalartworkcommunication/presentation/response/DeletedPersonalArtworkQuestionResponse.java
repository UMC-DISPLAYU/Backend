package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkQuestionResponse(
    Long personalQuestionId, LocalDateTime deletedAt) {}
