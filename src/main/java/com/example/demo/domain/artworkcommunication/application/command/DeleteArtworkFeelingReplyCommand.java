package com.example.demo.domain.artworkcommunication.application.command;

public record DeleteArtworkFeelingReplyCommand(
    Long displayArtworkId, Long feelingId, Long feelingReplyId, Long userId) {}
