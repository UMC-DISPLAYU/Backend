package com.example.demo.domain.display.presentation.response;

import com.example.demo.domain.display.domain.type.DisplayStatus;

public record DisplayRejectionReasonResponse(
    Long displayId, DisplayStatus publishStatus, String rejectionReason) {}
