package com.example.demo.domain.artworkcommunication.application.command;

public record DeleteArtworkQuestionCommand(
        Long displayArtworkId,
        Long questionId,
        Long userId
) {
}
