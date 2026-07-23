package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkFeelingCommand(Long personalArtworkId, Long userId, String content) {}
