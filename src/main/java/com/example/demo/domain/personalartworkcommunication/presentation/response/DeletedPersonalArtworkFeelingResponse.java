package com.example.demo.domain.personalartworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkFeelingResponse(
    Long personalFeelingId, LocalDateTime deletedAt) {}
