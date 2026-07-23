package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkFeelingLikeCommand(
    Long personalArtworkId, Long personalFeelingId, Long userId) {}
