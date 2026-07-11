package com.example.demo.domain.user.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record SignupRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{5,15}$",
                message = "닉네임은 한글, 영문, 숫자로 5~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.")
        String nickname,
        @NotEmpty(message = "약관 동의 목록은 비어있을 수 없습니다.") List<AgreementRequest> agreements) {

    public record AgreementRequest(
            @NotNull(message = "agreeId는 필수입니다.") Long agreeId, boolean isAgreed) {}
}