package com.example.demo.domain.displayartwork.application.result;

public record AuthorSetupResult(
    Long artworkId, String artistName, int coAuthorCount, Long qaHandlerUserId) {}
