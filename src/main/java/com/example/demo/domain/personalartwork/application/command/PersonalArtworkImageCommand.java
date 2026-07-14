package com.example.demo.domain.personalartwork.application.command;

import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;

public record PersonalArtworkImageCommand(
    String imageUrl,
    boolean isThumbnail,
    ArtworkImageType imageType,
    int sortOrder,
    String caption,
    int width,
    int height) {}
