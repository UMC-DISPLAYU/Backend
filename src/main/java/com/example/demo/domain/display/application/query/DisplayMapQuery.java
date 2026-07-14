package com.example.demo.domain.display.application.query;

import java.math.BigDecimal;

public record DisplayMapQuery(
    BigDecimal southLatitude,
    BigDecimal westLongitude,
    BigDecimal northLatitude,
    BigDecimal eastLongitude,
    String searchWord,
    Long cursor,
    int size) {}
