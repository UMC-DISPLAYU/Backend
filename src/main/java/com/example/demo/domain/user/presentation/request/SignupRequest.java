package com.example.demo.domain.user.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record SignupRequest(
    @Schema(description = "사용할 닉네임. 한글, 영문, 숫자만 사용하여 5~15자로 입력합니다.", example = "maya01")
        @NotBlank(message = "닉네임은 필수입니다.") @Pattern(
            regexp = "^[가-힣a-zA-Z0-9]{5,15}$",
            message = "닉네임은 한글, 영문, 숫자로 5~15자여야 하며 공백과 특수문자는 사용할 수 없습니다.")
        String nickname,
    @Schema(description = "약관 동의 목록. GET /api/v1/agreements에서 받은 ID를 사용하며 필수 약관에 모두 동의해야 합니다.")
        List<@Valid AgreementRequest> agreements) {

  public record AgreementRequest(
      @Schema(description = "GET /api/v1/agreements에서 받은 환경별 실제 약관 ID")
          @NotNull(message = "agreeId는 필수입니다.") Long agreeId,
      @Schema(description = "약관 동의 여부", example = "true") boolean isAgreed) {}
}
