package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkQuestionLikeCommand(Long displayArtworkId, Long questionId, Long userId) {}
