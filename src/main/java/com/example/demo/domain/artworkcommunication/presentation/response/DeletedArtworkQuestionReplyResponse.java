package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedArtworkQuestionReplyResponse(Long questionReplyId, LocalDateTime deletedAt) {}
