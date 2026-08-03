package com.example.demo.domain.displayartwork.application.result;

import java.util.List;

public record AuthorSetupResult(
    Long artworkId,
    String artistName,
    Long artistUserId,
    int coAuthorCount,
    List<Long> qaHandlerUserIds) {}
