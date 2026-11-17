package com.example.demo.domain.report.presentation.request;

import com.example.demo.domain.report.domain.type.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateContentReportRequest(
    @NotNull @Schema(
            description =
                "신고 사유. SPAM_ADVERTISEMENT=스팸·광고, ABUSE_DEFAMATION=욕설·비방, OBSCENE_OFFENSIVE_CONTENT=음란·불쾌한 내용, PERSONAL_INFORMATION_EXPOSURE=개인정보 노출, OTHER=기타. 기타도 추가 텍스트 없이 접수합니다.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        ReportReason reason) {}
