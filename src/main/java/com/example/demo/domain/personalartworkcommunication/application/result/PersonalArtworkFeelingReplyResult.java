package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record PersonalArtworkFeelingReplyResult(
    Long personalFeelingReplyId,
    LocalDateTime createdAt,
    String content,
    Long personalFeelingId,
    Long userId,
    String nickname) {}
