package com.example.demo.domain.personalartworkcommunication.application.command;

public record DeletePersonalArtworkFeelingCommand(
    Long personalArtworkId, Long personalFeelingId, Long userId) {}
