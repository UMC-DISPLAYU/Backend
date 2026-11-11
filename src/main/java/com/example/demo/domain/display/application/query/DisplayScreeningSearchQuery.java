package com.example.demo.domain.display.application.query;

import com.example.demo.domain.display.domain.type.DisplayStatus;

public record DisplayScreeningSearchQuery(String q, DisplayStatus status, Long cursor, int size) {}
