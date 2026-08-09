package com.example.demo.domain.personalartworkcommunication.application.query;

public record GetPersonalArtworkQuestionsQuery(
    Long personalArtworkId, Long cursorId, int size, Long userId) {}
