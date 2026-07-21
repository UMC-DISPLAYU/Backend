package com.example.demo.domain.personalartworkcommunication.application.command;

public record CreatePersonalArtworkFeelingCommand(
    Long personalArtworkId, Long userId, String content) {}
