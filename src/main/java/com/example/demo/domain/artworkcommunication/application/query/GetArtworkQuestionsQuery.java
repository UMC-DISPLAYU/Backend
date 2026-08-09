package com.example.demo.domain.artworkcommunication.application.query;

public record GetArtworkQuestionsQuery(
    Long displayArtworkId, Long cursorId, int size, Long userId) {}
