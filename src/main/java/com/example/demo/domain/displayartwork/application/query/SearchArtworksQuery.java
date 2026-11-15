package com.example.demo.domain.displayartwork.application.query;

import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;

public record SearchArtworksQuery(
    String searchWord, DisplayType type, SearchDisplayStatus status, int page, int size) {}
