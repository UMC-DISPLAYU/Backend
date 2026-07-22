package com.example.demo.domain.personalartworkcommunication.application.command;

public record CreatePersonalArtworkQuestionCommand(
    Long personalArtworkId, Long userId, String content, boolean isPublic) {}
