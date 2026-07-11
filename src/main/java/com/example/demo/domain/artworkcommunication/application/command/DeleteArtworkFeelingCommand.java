package com.example.demo.domain.artworkcommunication.application.command;

public record DeleteArtworkFeelingCommand(
        Long displayArtworkId,
        Long feelingId,
        Long userId
) {
}
