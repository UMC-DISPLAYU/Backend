package com.example.demo.domain.personalartworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedPersonalArtworkFeelingResult(
    Long personalFeelingId, LocalDateTime deletedAt) {}
