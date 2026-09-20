package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.type.DisplayStatus;

public record DisplayRejectionReasonResult(
    Long displayId, DisplayStatus publishStatus, String rejectionReason) {}
