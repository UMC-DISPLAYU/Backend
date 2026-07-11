package com.example.demo.domain.artworkcommunication.application.command;

public record UpdateArtworkQuestionCommand(
        Long displayArtworkId,
        Long questionId,
        Long userId,
        String content,
        Boolean isPublic
) {
}
