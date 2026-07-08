package com.example.demo.user.presentation.request;

import java.util.List;

public record SignupRequest(
        String nickname,
        List<AgreementRequest> agreements
) {

    public record AgreementRequest(
            Long agreeId,
            boolean isAgreed
    ) {}
}
