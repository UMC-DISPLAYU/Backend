package com.example.demo.domain.personalartworkcommunication.application.command;

public record PersonalArtworkFeelingReplyLikeCommand(
    Long personalArtworkId, Long personalFeelingId, Long personalFeelingReplyId, Long userId) {}
