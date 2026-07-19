package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;

public record ArtworkImageCommand(
    String imageUrl,
    boolean isThumbnail,
    ArtworkImageType imageType,
    int sortOrder,
    String caption,
    int width,
    int height) {}
