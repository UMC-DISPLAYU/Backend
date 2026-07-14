package com.example.demo.domain.displayartwork.application.command;

import java.util.List;

public record AuthorSetupCommand(
    Long artworkId,
    String artistName,
    List<Long> coAuthorUserIds,
    List<String> coAuthorRawNames,
    Long qaHandlerUserId) {}
