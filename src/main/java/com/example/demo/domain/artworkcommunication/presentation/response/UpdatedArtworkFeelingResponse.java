package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record UpdatedArtworkFeelingResponse(
        Long feelingId,
        String content,
        LocalDateTime updatedAt
) {
}
