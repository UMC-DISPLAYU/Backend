package com.example.demo.domain.artworkcommunication.application.result;

import java.time.LocalDateTime;

public record UpdatedArtworkFeelingResult(
        Long feelingId,
        String content,
        LocalDateTime updatedAt
) {
}
