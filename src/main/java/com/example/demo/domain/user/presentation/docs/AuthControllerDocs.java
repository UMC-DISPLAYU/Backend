package com.example.demo.domain.user.presentation.docs;

import static com.example.demo.domain.user.presentation.docs.UserApiDocs.SIGNUP_REQUEST_EXAMPLE;
import static com.example.demo.domain.user.presentation.docs.UserApiDocs.SIGNUP_SUCCESS_EXAMPLE;

import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "User", description = "사용자 인증 API")
public interface AuthControllerDocs {

  @Operation(
      summary = "회원가입",
      description =
          """
                    OAuth 인증이 완료된 사용자가 회원가입을 진행합니다.

                    - HttpOnly Cookie의 signupToken을 통해 인증된 사용자임을 확인합니다.
                    - 기존 Authorization Bearer 방식도 계속 지원합니다.
                    - Swagger Authorize에는 Bearer 접두사 없이 signupToken 값만 입력합니다.
                    - GET /api/v1/agreements에서 받은 서비스 이용약관의 code와 version을 사용해야 합니다.
                    - 회원가입 필수 동의는 서비스 이용약관(TERMS_OF_SERVICE) 하나입니다.
                    - 개인정보 수집·이용 안내와 개인정보 처리방침은 조회 전용이며 회원가입 요청에 포함하지 않습니다.
                    - 위치기반서비스 이용약관은 신규 회원가입 동의 대상이 아닙니다.
                    - 만 14세 이상 확인(isOver14)은 반드시 true여야 합니다.
                    - 회원가입 완료 후 사용자 정보를 반환합니다.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "회원가입 요청 (약관 목록 조회 API에서 받은 code와 version 사용)",
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "필수 약관 동의 회원가입", value = SIGNUP_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = "회원가입 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "회원가입 성공", value = SIGNUP_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "400",
      description = "요청값 또는 약관 동의 오류",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "입력값 검증 실패", value = SIGNUP_INVALID_INPUT_EXAMPLE),
                @ExampleObject(name = "필수 약관 미동의", value = REQUIRED_AGREEMENT_EXAMPLE),
                @ExampleObject(name = "잘못된 약관 버전", value = AGREEMENT_NOT_FOUND_EXAMPLE),
                @ExampleObject(name = "중복 약관", value = DUPLICATE_AGREEMENT_EXAMPLE)
              }))
  @ApiResponse(
      responseCode = "401",
      description = "회원가입 토큰 누락 또는 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "회원가입 토큰 오류", value = INVALID_SIGNUP_TOKEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "409",
      description = "이미 가입한 사용자 또는 닉네임 중복",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(name = "이미 가입한 사용자", value = ALREADY_REGISTERED_EXAMPLE),
                @ExampleObject(name = "닉네임 중복", value = SIGNUP_DUPLICATE_NICKNAME_EXAMPLE)
              }))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<SignupResponse.Signup> signup(
      SignupRequest request,
      @Parameter(hidden = true) String authorization,
      @Parameter(hidden = true) String cookieSignupToken,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse);

  String SIGNUP_INVALID_INPUT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_INPUT_VALUE", "message": "입력값이 올바르지 않습니다.", "details": [{ "field": "nickname", "message": "닉네임은 한글, 영문, 숫자로 2~15자여야 하며 공백과 특수문자는 사용할 수 없습니다." }] }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/signup" } }
      """;
  String REQUIRED_AGREEMENT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "REQUIRED_AGREEMENT_NOT_ACCEPTED", "message": "필수 약관에 동의해야 회원가입할 수 있습니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/signup" } }
      """;
  String AGREEMENT_NOT_FOUND_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "AGREEMENT_NOT_FOUND", "message": "존재하지 않는 약관입니다.", "details": null }, "meta": { "timestamp": "2026-11-21T00:00:00", "path": "/api/v1/auth/signup" } }
      """;
  String DUPLICATE_AGREEMENT_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "DUPLICATE_AGREEMENT", "message": "동일한 약관이 중복으로 전달되었습니다.", "details": null }, "meta": { "timestamp": "2026-11-21T00:00:00", "path": "/api/v1/auth/signup" } }
      """;
  String INVALID_SIGNUP_TOKEN_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_SIGNUP_TOKEN", "message": "유효하지 않은 회원가입 토큰입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/signup" } }
      """;
  String ALREADY_REGISTERED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ALREADY_REGISTERED_USER", "message": "이미 가입된 사용자입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/signup" } }
      """;
  String SIGNUP_DUPLICATE_NICKNAME_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "DUPLICATE_NICKNAME", "message": "이미 사용 중인 닉네임입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/signup" } }
      """;
}
