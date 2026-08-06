package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkQuestionLikeCommand(
    Long personalArtworkId, Long personalQuestionId, Long userId) {}
