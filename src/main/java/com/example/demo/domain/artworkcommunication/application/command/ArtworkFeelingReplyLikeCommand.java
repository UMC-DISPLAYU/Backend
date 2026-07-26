package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkFeelingReplyLikeCommand(
    Long displayArtworkId, Long feelingId, Long feelingReplyId, Long userId) {}
