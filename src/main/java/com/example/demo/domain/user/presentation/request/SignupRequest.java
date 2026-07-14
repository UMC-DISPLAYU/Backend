package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record SignupRequest(
    @Schema(description = "사용할 닉네임", example = "maya")
        @NotBlank(message = "닉네임은 필수입니다.") @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]{2,15}$",
            message = "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.")
        String nickname,
    @Schema(description = "약관 동의 목록") @NotEmpty(message = "약관 동의 목록은 비어있을 수 없습니다.") List<AgreementRequest> agreements) {

  public record AgreementRequest(
      @Schema(description = "약관 ID", example = "1") @NotNull(message = "agreeId는 필수입니다.") Long agreeId,
      @Schema(description = "약관 동의 여부", example = "true") boolean isAgreed) {}
}
