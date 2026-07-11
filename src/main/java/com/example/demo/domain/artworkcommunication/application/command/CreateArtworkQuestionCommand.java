package com.example.demo.domain.artworkcommunication.application.command;

public record CreateArtworkQuestionCommand(
        Long displayArtworkId,
        Long userId,
        String content,
        Boolean isPublic
) {
}
