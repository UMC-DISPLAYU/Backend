package com.example.demo.user.presentation.docs;

import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.user.presentation.request.SignupRequest;
import com.example.demo.user.presentation.response.SignupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "인증 API")
public interface AuthControllerDocs {

  @Operation(
      summary = "회원가입 API",
      description =
          """
                    ## 회원가입

                    OAuth 인증이 완료된 사용자가 닉네임과 약관 동의를 입력하여 회원가입합니다.

                    ### 요청 사항
                    - nickname : 사용할 닉네임
                    - agreements : 약관 동의 목록
                    """)
  @RequestBody(
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "회원가입 요청",
                      value =
                          """
                                    {
                                      "nickname": "maya",
                                      "agreements": [
                                        {
                                          "agreeId": 1,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 2,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 3,
                                          "isAgreed": false
                                        },
                                        {
                                          "agreeId": 4,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 5,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 6,
                                          "isAgreed": false
                                        },
                                        {
                                          "agreeId": 7,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 8,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 9,
                                          "isAgreed": true
                                        },
                                        {
                                          "agreeId": 10,
                                          "isAgreed": false
                                        }
                                      ]
                                    }
                                    """)))
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "회원가입 성공",
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
                                                  "user": {
                                                    "id": 14,
                                                    "provider": "Kakao",
                                                    "name": "마야",
                                                    "nickname": "maya",
                                                    "socialEmail": "maya@gmail.com",
                                                    "schoolEmail": null,
                                                    "isVerified": false
                                                  },
                                                  "accessToken": null,
                                                  "refreshToken": null
                                                },
                                                "error": null,
                                                "meta": {
                                                  "timestamp": "2026-07-10T13:07:34.3156433",
                                                  "path": "/api/v1/auth/signup"
                                                }
                                              }
                                            }
                                            """))),
    @ApiResponse(
        responseCode = "400",
        description = "필수 약관 미동의",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                                            {
                                              "resultType": "FAIL",
                                              "fail": {
                                                "data": null,
                                                "error": {
                                                  "status": 400,
                                                  "code": "REQUIRED_AGREEMENT_NOT_ACCEPTED",
                                                  "message": "필수 약관에 동의해야 회원가입할 수 있습니다."
                                                },
                                                "meta": {
                                                  "timestamp": "2026-07-10T13:15:22.123456",
                                                  "path": "/api/v1/auth/signup"
                                                }
                                              }
                                            }
                                            """))),
    @ApiResponse(
        responseCode = "409",
        description = "닉네임 중복",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                                            {
                                              "resultType": "FAIL",
                                              "fail": {
                                                "data": null,
                                                "error": {
                                                  "status": 409,
                                                  "code": "DUPLICATE_NICKNAME",
                                                  "message": "이미 사용 중인 닉네임입니다."
                                                },
                                                "meta": {
                                                  "timestamp": "2026-07-10T13:15:22.123456",
                                                  "path": "/api/v1/auth/signup"
                                                }
                                              }
                                            }
                                            """))),
    @ApiResponse(
        responseCode = "409",
        description = "이미 가입된 사용자",
        content =
            @Content(
                mediaType = "application/json",
                examples =
                    @ExampleObject(
                        value =
                            """
                                            {
                                              "resultType": "FAIL",
                                              "fail": {
                                                "data": null,
                                                "error": {
                                                  "status": 409,
                                                  "code": "ALREADY_REGISTERED_USER",
                                                  "message": "이미 가입된 사용자입니다."
                                                },
                                                "meta": {
                                                  "timestamp": "2026-07-10T13:15:22.123456",
                                                  "path": "/api/v1/auth/signup"
                                                }
                                              }
                                            }
                                            """)))
  })
  ApiResponseBody<SignupResponse.Signup> signup(
      SignupRequest request, HttpServletRequest httpRequest);
}
