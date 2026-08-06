package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkQuestionReplyResult(
    Long personalQuestionReplyId, LocalDateTime deletedAt) {}
