package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.VerifySchoolEmailRequest;
import com.example.demo.domain.user.presentation.response.SchoolEmailVerificationConfirmResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
                              - 학교명은 GET /api/v1/schools 검색 결과에서 선택한 값을 사용합니다.
                              - 서버 대학교 목록에 없는 학교명은 사용할 수 없습니다.
                              - 선택한 학교와 학교 이메일 도메인의 일치 여부를 확인합니다.
                              - 입력한 학교 이메일로 인증번호를 발송합니다.
                              - 인증번호 재요청 시 1분 쿨다운이 적용됩니다.

                              인증번호 확인 완료 후 작가 인증이 완료됩니다.
                              """)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "학교 이메일 인증번호 발송 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "발송 성공", value = SEND_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "이메일, 학교 또는 인증 상태 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이메일 형식 오류", value = SEND_INVALID_EMAIL_EXAMPLE),
                @ExampleObject(name = "지원하지 않는 학교", value = SEND_UNSUPPORTED_UNIVERSITY_EXAMPLE),
                @ExampleObject(name = "학교 웹메일 불일치", value = SEND_EMAIL_MISMATCH_EXAMPLE),
                @ExampleObject(name = "이미 인증된 사용자", value = SEND_ALREADY_VERIFIED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = SEND_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "사용자 없음", value = SEND_USER_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 사용 중인 학교 이메일",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "학교 이메일 중복", value = SEND_DUPLICATE_EMAIL_EXAMPLE)))
  @ApiResponse(
      responseCode = "429",
      description = "재요청 쿨다운",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "발송 쿨다운", value = SEND_COOLDOWN_EXAMPLE)))
  @ApiResponse(
      responseCode = "500",
      description = "인증 이메일 발송 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "메일 발송 실패", value = SEND_FAILED_EXAMPLE)))
  ApiResponseBody<Void> send(
      SchoolEmailVerificationRequest request, AuthUser user, HttpServletRequest httpRequest)
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
  @ApiResponse(
      responseCode = "200",
      description = "학교 이메일 인증번호 재발송 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "재발송 성공", value = RESEND_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "이메일 형식 오류 또는 이미 인증 완료",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이메일 형식 오류", value = RESEND_INVALID_EMAIL_EXAMPLE),
                @ExampleObject(name = "이미 인증 완료", value = RESEND_ALREADY_VERIFIED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = RESEND_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "발급된 인증번호 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증번호 없음", value = RESEND_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "429",
      description = "재요청 쿨다운",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "발송 쿨다운", value = RESEND_COOLDOWN_EXAMPLE)))
  ApiResponseBody<Void> resend(
      ResendSchoolEmailVerificationRequest request, AuthUser user, HttpServletRequest httpRequest);

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
  @ApiResponse(
      responseCode = "200",
      description = "학교 이메일 인증번호 확인 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 성공", value = CONFIRM_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "이메일, 인증번호 또는 인증 상태 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "인증번호 불일치", value = CONFIRM_CODE_MISMATCH_EXAMPLE),
                @ExampleObject(name = "인증번호 만료", value = CONFIRM_CODE_EXPIRED_EXAMPLE),
                @ExampleObject(name = "확인 횟수 초과", value = CONFIRM_ATTEMPTS_EXCEEDED_EXAMPLE),
                @ExampleObject(name = "이미 인증 완료", value = CONFIRM_ALREADY_VERIFIED_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "인증 정보 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증 필요", value = CONFIRM_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "발급된 인증번호 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "인증번호 없음", value = CONFIRM_NOT_FOUND_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 사용 중인 학교 이메일",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "학교 이메일 중복", value = CONFIRM_DUPLICATE_EMAIL_EXAMPLE)))
  ApiResponseBody<SchoolEmailVerificationConfirmResponse> confirm(
      VerifySchoolEmailRequest request, AuthUser user, HttpServletRequest httpRequest);

  String SEND_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/users/me/verification/email/send" }
      }
      """;

  String RESEND_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/users/me/verification/email/resend" }
      }
      """;

  String SEND_EMAIL_MISMATCH_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "SCHOOL_EMAIL_DOMAIN_MISMATCH",
          "message": "선택한 학교의 웹메일과 일치하지 않습니다.",
          "details": null
        },
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/users/me/verification/email/send" }
      }
      """;

  String CONFIRM_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "schoolEmail": "maya@duksung.ac.kr",
            "isVerified": true
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/users/me/verification/email/confirm" }
      }
      """;

  String SEND_INVALID_EMAIL_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_EMAIL\",\"message\":\"유효하지 않은 학교 이메일입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_UNSUPPORTED_UNIVERSITY_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNSUPPORTED_UNIVERSITY\",\"message\":\"지원하지 않는 대학교입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_ALREADY_VERIFIED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_VERIFIED_USER\",\"message\":\"이미 작가 인증이 완료된 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_USER_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"USER_NOT_FOUND\",\"message\":\"사용자를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_DUPLICATE_EMAIL_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DUPLICATE_SCHOOL_EMAIL\",\"message\":\"이미 다른 계정에서 작가 인증에 사용된 학교 이메일입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_COOLDOWN_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"EMAIL_SEND_COOLDOWN\",\"message\":\"인증 이메일은 잠시 후 다시 요청할 수 있습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";
  String SEND_FAILED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"EMAIL_SEND_FAILED\",\"message\":\"인증 이메일 발송에 실패했습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/send\"}}";

  String RESEND_INVALID_EMAIL_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"INVALID_EMAIL\",\"message\":\"유효하지 않은 학교 이메일입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/resend\"}}";
  String RESEND_ALREADY_VERIFIED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_VERIFIED_USER\",\"message\":\"이미 작가 인증이 완료된 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/resend\"}}";
  String RESEND_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/resend\"}}";
  String RESEND_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"EMAIL_VERIFICATION_NOT_FOUND\",\"message\":\"발급된 인증번호를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/resend\"}}";
  String RESEND_COOLDOWN_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"EMAIL_SEND_COOLDOWN\",\"message\":\"인증 이메일은 잠시 후 다시 요청할 수 있습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/resend\"}}";

  String CONFIRM_CODE_MISMATCH_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"VERIFICATION_CODE_MISMATCH\",\"message\":\"인증번호가 일치하지 않습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_CODE_EXPIRED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"VERIFICATION_CODE_EXPIRED\",\"message\":\"인증번호가 만료되었습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_ATTEMPTS_EXCEEDED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"VERIFICATION_ATTEMPTS_EXCEEDED\",\"message\":\"인증번호 확인 실패 횟수를 초과했습니다. 인증번호를 다시 요청해 주세요.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_ALREADY_VERIFIED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"ALREADY_VERIFIED_USER\",\"message\":\"이미 작가 인증이 완료된 사용자입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_UNAUTHORIZED_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"UNAUTHORIZED\",\"message\":\"인증이 필요합니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_NOT_FOUND_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"EMAIL_VERIFICATION_NOT_FOUND\",\"message\":\"발급된 인증번호를 찾을 수 없습니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
  String CONFIRM_DUPLICATE_EMAIL_EXAMPLE =
      "{\"resultType\":\"FAIL\",\"success\":null,\"error\":{\"code\":\"DUPLICATE_SCHOOL_EMAIL\",\"message\":\"이미 다른 계정에서 작가 인증에 사용된 학교 이메일입니다.\",\"details\":null},\"meta\":{\"timestamp\":\"2026-11-14T04:13:15.651\",\"path\":\"/api/v1/users/me/verification/email/confirm\"}}";
}
