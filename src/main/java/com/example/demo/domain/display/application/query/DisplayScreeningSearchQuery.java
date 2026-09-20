package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.DisplayStatus;

public record DisplayScreeningSearchQuery(DisplayStatus status, Long cursor, int size) {}
