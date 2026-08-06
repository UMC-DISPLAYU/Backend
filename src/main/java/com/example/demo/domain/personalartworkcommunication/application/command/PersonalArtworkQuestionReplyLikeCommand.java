package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkQuestionReplyLikeCommand(
    Long personalArtworkId, Long personalQuestionId, Long personalQuestionReplyId, Long userId) {}
