package com.example.demo.domain.personalartworkcommunication.application.query;

public record GetPersonalArtworkFeelingRepliesQuery(
    Long personalArtworkId, Long personalFeelingId, Long cursorId) {}
