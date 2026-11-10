package com.example.demo.domain.display.presentation.response;

public record CancelDisplayScreeningResponse(
    Long displayId, Long screeningId, String status, String screeningStatus) {}
