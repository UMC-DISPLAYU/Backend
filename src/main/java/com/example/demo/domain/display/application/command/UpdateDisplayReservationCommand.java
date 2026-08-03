package com.example.demo.domain.display.application.command;

import com.example.demo.domain.display.domain.type.ContentOpenPolicy;

public record UpdateDisplayReservationCommand(
    Long userId,
    Long displayId,
    ContentOpenPolicy artworkContentOpen,
    ContentOpenPolicy exhibitionContentOpen) {}
