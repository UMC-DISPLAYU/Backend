package com.example.demo.domain.personalartworkcommunication.application.command;

public record DeletePersonalArtworkQuestionCommand(
    Long personalArtworkId, Long personalQuestionId, Long userId) {}
