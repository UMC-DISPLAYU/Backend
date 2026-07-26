package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingReplyResponse(
    Long personalFeelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalFeelingId,
    Long userId,
    String nickname) {}
