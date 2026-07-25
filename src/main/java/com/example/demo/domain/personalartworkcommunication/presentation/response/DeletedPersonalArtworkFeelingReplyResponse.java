package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkFeelingReplyResponse(
    Long personalFeelingReplyId, LocalDateTime deletedAt) {}
