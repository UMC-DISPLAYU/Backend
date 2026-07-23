package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkFeelingReplyCommand(
    Long personalArtworkId, Long personalFeelingId, Long userId, String content) {}
