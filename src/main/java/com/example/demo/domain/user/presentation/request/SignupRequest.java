package com.example.demo.domain.user.presentation.request;

import com.example.demo.domain.user.domain.enums.AgreementCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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
    @Schema(description = "동의한 약관 목록. GET /api/v1/agreements에서 받은 code와 version을 사용합니다.")
        List<@Valid AgreementRequest> agreements,
    @Schema(description = "만 14세 이상 확인", example = "true")
        @NotNull(message = "만 14세 이상 확인은 필수입니다.") @AssertTrue(message = "만 14세 이상이어야 회원가입할 수 있습니다.") Boolean isOver14) {

  public record AgreementRequest(
      @Schema(description = "약관 코드", example = "TERMS_OF_SERVICE")
          @NotNull(message = "약관 코드는 필수입니다.") AgreementCode code,
      @Schema(description = "약관 버전", example = "1.0") @NotBlank(message = "약관 버전은 필수입니다.") String version) {}
}
