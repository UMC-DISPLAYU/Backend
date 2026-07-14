package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkFeelingReplyCommand(
    Long displayArtworkId, Long feelingId, Long userId, String content) {}
