package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkQuestionReplyLikeCommand(
    Long displayArtworkId, Long questionId, Long questionReplyId, Long userId) {}
