package com.example.demo.domain.displayartwork.presentation.response;

public record AuthorSetupResponse(
    Long artworkId, String artistName, int coAuthorCount, Long qaHandlerUserId) {}
