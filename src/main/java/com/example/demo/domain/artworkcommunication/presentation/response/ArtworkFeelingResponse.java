package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record ArtworkFeelingResponse(
        Long feelingId,
        Long userId,
        String content,
        LocalDateTime createdAt
) {
}
