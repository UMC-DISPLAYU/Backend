package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record ArtworkFeelingReplyResult(
    Long feelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long feelingId,
    Long userId,
    String nickname) {}
