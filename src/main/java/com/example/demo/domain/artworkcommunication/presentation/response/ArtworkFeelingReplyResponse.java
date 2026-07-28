package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkFeelingReplyResponse(
    Long feelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long feelingId,
    Long userId,
    String nickname) {}
