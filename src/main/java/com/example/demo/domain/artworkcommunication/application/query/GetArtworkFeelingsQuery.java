package com.example.demo.domain.artworkcommunication.application.query;

public record GetArtworkFeelingsQuery(
    Long displayArtworkId, Long cursorId, int size, Long viewerUserId) {}
