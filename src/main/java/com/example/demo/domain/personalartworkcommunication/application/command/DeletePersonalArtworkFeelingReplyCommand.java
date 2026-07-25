package com.example.demo.domain.personalartworkcommunication.application.command;

public record DeletePersonalArtworkFeelingReplyCommand(
    Long personalArtworkId, Long personalFeelingId, Long personalFeelingReplyId, Long userId) {}
