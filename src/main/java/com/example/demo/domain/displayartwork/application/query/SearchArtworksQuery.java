package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.display.domain.type.DisplaySearchSort;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;

public record SearchArtworksQuery(
    String searchWord,
    DisplayType type,
    SearchDisplayStatus status,
    ArtworkType field,
    DisplaySearchSort sort,
    int page,
    int size) {}
