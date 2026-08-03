package com.example.demo.domain.personalartworkcommunication.application.command;

public record DeletePersonalArtworkQuestionReplyCommand(
    Long personalArtworkId, Long personalQuestionId, Long personalQuestionReplyId, Long userId) {}
