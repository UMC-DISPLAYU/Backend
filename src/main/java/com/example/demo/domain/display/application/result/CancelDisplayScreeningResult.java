package com.example.demo.domain.display.application.result;

public record CancelDisplayScreeningResult(
    Long displayId, Long screeningId, String status, String screeningStatus) {}
