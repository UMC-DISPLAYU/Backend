package com.example.demo.domain.artworkcommunication.application.command;

public record ArtworkFeelingCommand(Long displayArtworkId, Long userId, String content) {}
