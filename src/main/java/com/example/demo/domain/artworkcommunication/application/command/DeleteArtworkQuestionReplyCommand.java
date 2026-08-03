package com.example.demo.domain.artworkcommunication.application.command;

public record DeleteArtworkQuestionReplyCommand(
    Long displayArtworkId, Long questionId, Long questionReplyId, Long userId) {}
