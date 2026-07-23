package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkQuestionCommand(
    Long personalArtworkId, Long userId, String content, boolean isPublic) {}
