package com.example.demo.domain.user.presentation.docs;

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
                    - GET /api/v1/agreements에서 받은 실제 약관 ID를 사용해야 합니다.
                    - 위치 기반 서비스 약관, 서비스 이용약관, 개인정보 처리방침에 모두 동의해야 합니다.
                    - 필수 약관이 누락되거나 isAgreed=false이면 REQUIRED_AGREEMENT_NOT_ACCEPTED가 발생합니다.
                    - 마케팅 정보 수신 동의는 선택입니다.
                    - 회원가입 완료 후 사용자 정보를 반환합니다.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "회원가입 요청 (약관 목록 조회 API에서 받은 ID 사용)",
      required = true,
      content = @Content(mediaType = "application/json"))
  @ApiResponse(
      responseCode = "200",
      description = "회원가입 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "회원가입 성공", value = SIGNUP_SUCCESS_EXAMPLE)))
  @SecurityRequirement(name = "Authorization")
  ApiResponseBody<SignupResponse.Signup> signup(
      SignupRequest request,
      @Parameter(hidden = true) String authorization,
      @Parameter(hidden = true) String cookieSignupToken,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse);
}
