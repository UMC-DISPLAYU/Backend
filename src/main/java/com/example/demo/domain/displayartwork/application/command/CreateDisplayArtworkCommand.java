package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.util.List;

public record CreateDisplayArtworkCommand(
    Long displayId,
    String artworkName,
    String content,
    ArtworkType type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    List<ArtworkImageCommand> images) {}
