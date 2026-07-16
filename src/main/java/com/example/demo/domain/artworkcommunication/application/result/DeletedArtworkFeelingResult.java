package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record DeletedArtworkFeelingResult(Long feelingId, LocalDateTime deletedAt) {}
