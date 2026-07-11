package com.example.demo.domain.user.presentation.docs;



import com.example.demo.domain.user.presentation.request.SocialLoginRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "로그인 API")
public interface LoginControllerDocs {


    @Operation(
            summary = "소셜 로그인 API",
            description =
                    """
                    ## 소셜 로그인

                    카카오/구글 등 소셜 플랫폼에서 발급받은 idToken을 검증하여 로그인합니다.

                    ### 처리 방식
                    - 기존 회원
                      - accessToken, refreshToken 발급
                      - 사용자 정보 반환

                    - 신규 회원
                      - 회원가입 진행을 위한 signupToken 발급
                      - nickname 입력 후 회원가입 API 진행
                    """)
    @RequestBody(
            required = true,
            content =
            @Content(
                    mediaType = "application/json",
                    examples =
                    @ExampleObject(
                            name = "카카오 로그인 요청",
                            value =
                                    """
                                    {
                                      "provider": "Kakao",
                                      "idToken": "eyJhbGciOi..."
                                    }
                                    """)))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseBody.class),
                            examples =
                            @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "SUCCESS",
                                              "success": {
                                                "data": {
                                                  "isNewUser": false,
                                                  "accessToken": "eyJhbGciOi...",
                                                  "refreshToken": "eyJhbGciOi...",
                                                  "user": {
                                                    "id": 1,
                                                    "provider": "Kakao",
                                                    "providerId": "123456789",
                                                    "name": "마야",
                                                    "nickname": "maya",
                                                    "socialEmail": "maya@gmail.com",
                                                    "schoolEmail": "maya@duksung.ac.kr",
                                                    "isVerified": false
                                                  }
                                                },
                                                "error": null,
                                                "meta": {
                                                  "timestamp": "2026-06-30T22:10:00",
                                                  "path": "/api/v1/auth/login"
                                                }
                                              }
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "200",
                    description = "신규 회원 - 회원가입 필요",
                    content =
                    @Content(
                            mediaType = "application/json",
                            examples =
                            @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "SUCCESS",
                                              "success": {
                                                "data": {
                                                  "isNewUser": true,
                                                  "signupToken": "eyJhbGciOi...",
                                                  "provider": "Kakao",
                                                  "name": "마야",
                                                  "socialEmail": "maya@gmail.com"
                                                },
                                                "error": null,
                                                "meta": {
                                                  "timestamp": "2026-06-30T22:10:00",
                                                  "path": "/api/v1/auth/login"
                                                }
                                              }
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 소셜 토큰",
                    content =
                    @Content(
                            mediaType = "application/json",
                            examples =
                            @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "FAIL",
                                              "success": null,
                                              "error": {
                                                "code": "INVALID_SOCIAL_TOKEN",
                                                "message": "유효하지 않은 소셜 토큰입니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/login"
                                              }
                                            }
                                            """)))
    })
    ApiResponseBody<?> login(
            SocialLoginRequest request,
            HttpServletRequest httpRequest
    );
}
