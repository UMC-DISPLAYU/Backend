package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkQuestionReplyCommand(
    Long personalArtworkId, Long personalQuestionId, Long userId, String content) {}
