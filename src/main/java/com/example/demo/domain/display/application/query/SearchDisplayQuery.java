package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;

public record SearchDisplayQuery(
    String searchWord,
    SearchDisplayStatus status,
    DisplayRegion region,
    DisplayField field,
    DisplayType type,
    Long cursor,
    int size) {}
