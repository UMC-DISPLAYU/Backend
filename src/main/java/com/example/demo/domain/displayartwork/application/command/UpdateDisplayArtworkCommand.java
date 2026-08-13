package com.example.demo.domain.displayartwork.application.command;

import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.util.List;

public record UpdateDisplayArtworkCommand(
    Long artworkId,
    String artworkName,
    String content,
    java.util.List<ArtworkType> types,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    List<ArtworkImageCommand> images,
    String artistName,
    Long artistUserId,
    List<Long> coAuthorUserIds,
    List<String> coAuthorRawNames,
    List<Long> qaHandlerUserIds) {}
