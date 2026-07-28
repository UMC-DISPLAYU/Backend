package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkFeelingReplyResult(
    Long personalFeelingReplyId, LocalDateTime deletedAt) {}
