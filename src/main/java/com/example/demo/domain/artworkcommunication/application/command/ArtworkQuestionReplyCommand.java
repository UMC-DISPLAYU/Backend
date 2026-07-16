package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkQuestionReplyCommand(
    Long displayArtworkId, Long questionId, Long userId, String content) {}
