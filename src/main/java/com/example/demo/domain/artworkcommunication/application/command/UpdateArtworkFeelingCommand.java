package com.example.demo.domain.artworkcommunication.application.command;

public record UpdateArtworkFeelingCommand(
    Long displayArtworkId, Long feelingId, Long userId, String content) {}
