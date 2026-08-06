package com.example.demo.domain.personalartworkcommunication.application.query;

public record GetPersonalArtworkFeelingsQuery(
    Long personalArtworkId, Long cursorId, int size, Long viewerUserId) {}
