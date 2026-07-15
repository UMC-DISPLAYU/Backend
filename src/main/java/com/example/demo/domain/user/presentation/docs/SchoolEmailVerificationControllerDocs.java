package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.VerifySchoolEmailRequest;
import com.example.demo.domain.user.presentation.response.SchoolEmailVerificationConfirmResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Tag(name = "School Email Verification", description = "학교 이메일 인증 API")
public interface SchoolEmailVerificationControllerDocs {

  @Operation(
      summary = "학교 이메일 인증번호 발송",
      description =
          """
                              작가 인증이 필요한 기능을 사용하기 위해 학교 이메일 인증번호를 발송합니다.

                              - 학교 이메일 형식을 검증합니다.
                              - 학교 이메일 도메인을 확인합니다.
                              - 입력한 학교 이메일로 인증번호를 발송합니다.
                              - 인증번호 재요청 시 1분 쿨다운이 적용됩니다.

                              인증번호 확인 완료 후 작가 인증이 완료됩니다.
                              """)
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<Void> send(SchoolEmailVerificationRequest request, HttpServletRequest httpRequest)
      throws IOException;

  @Operation(
      summary = "학교 이메일 인증번호 재발송",
      description =
          """
                              기존 발급된 인증번호를 기준으로 인증번호를 재발송합니다.

                              - 인증번호 발송 이력이 있어야 합니다.
                              - 이미 인증 완료된 이메일은 재발송할 수 없습니다.
                              - 인증번호 재요청 시 1분 쿨다운이 적용됩니다.
                              - 새로운 인증번호가 발급됩니다.

                              인증번호 확인 완료 후 작가 인증이 완료됩니다.
                              """)
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<Void> resend(
      ResendSchoolEmailVerificationRequest request, HttpServletRequest httpRequest);

  @Operation(
      summary = "학교 이메일 인증번호 확인",
      description =
          """
                              발송된 인증번호를 검증하여 학교 이메일 인증을 완료합니다.

                              - schoolEmail 기준으로 발급된 인증번호를 조회합니다.
                              - 인증번호 만료 여부를 확인합니다.
                              - 입력한 인증번호와 저장된 인증번호를 비교합니다.
                              - 인증 성공 시 인증 상태가 완료로 변경됩니다.

                              인증 완료 후 작가 인증 기능을 사용할 수 있습니다.
                              """)
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<SchoolEmailVerificationConfirmResponse> confirm(
      VerifySchoolEmailRequest request, HttpServletRequest httpRequest);
}
