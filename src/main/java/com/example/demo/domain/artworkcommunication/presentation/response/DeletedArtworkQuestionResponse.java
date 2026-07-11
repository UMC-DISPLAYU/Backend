package com.example.demo.domain.artworkcommunication.presentation.response;

import java.time.LocalDateTime;

public record DeletedArtworkQuestionResponse(
        Long artQueId,
        LocalDateTime deletedAt
) {
}
