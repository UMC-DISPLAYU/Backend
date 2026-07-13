package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedArtworkFeelingResponse(Long feelingId, LocalDateTime deletedAt) {}
