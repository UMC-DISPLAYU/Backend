package com.example.demo.domain.report.application.command;

import com.example.demo.domain.report.domain.type.ReportReason;

public record ReportArtworkFeelingCommand(
    Long artworkId, Long feelingId, Long reporterUserId, ReportReason reason) {}
