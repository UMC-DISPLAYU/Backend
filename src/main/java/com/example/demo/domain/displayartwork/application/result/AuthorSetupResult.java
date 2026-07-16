package com.example.demo.domain.displayartwork.application.result;

public record AuthorSetupResult(
    Long artworkId,
    String artistName,
    Long artistUserId,
    int coAuthorCount,
    Long qaHandlerUserId) {}
