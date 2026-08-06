package com.example.demo.domain.artworkcommunication.application.query;

import java.time.LocalDateTime;

public record MyArtworkFeelingQueryItem(
    Long itemId,
    int sourceOrder,
    Long artworkId,
    Long personalArtworkId,
    String artworkName,
    String content,
    LocalDateTime createdAt) {}
