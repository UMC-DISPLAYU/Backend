package com.example.demo.domain.personalartwork.application.command;

import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import java.util.List;

public record PersonalArtworkContentCommand(
    String artworkName,
    String content,
    ArtworkType type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    List<PersonalArtworkImageCommand> images) {}
