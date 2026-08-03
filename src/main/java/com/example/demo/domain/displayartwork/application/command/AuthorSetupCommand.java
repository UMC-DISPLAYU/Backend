package com.example.demo.domain.displayartwork.application.command;

import java.util.List;

public record AuthorSetupCommand(
    Long artworkId,
    String artistName,
    Long artistUserId,
    List<Long> coAuthorUserIds,
    List<String> coAuthorRawNames,
    List<Long> qaHandlerUserIds) {}
