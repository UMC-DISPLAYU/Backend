package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkQuestionReplyResponse(
    Long personalQuestionReplyId, LocalDateTime deletedAt) {}
