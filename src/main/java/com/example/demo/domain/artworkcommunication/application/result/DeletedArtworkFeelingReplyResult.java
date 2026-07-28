package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedArtworkFeelingReplyResult(Long feelingReplyId, LocalDateTime deletedAt) {}
