package com.example.demo.domain.user.presentation.docs;



import com.example.demo.domain.user.presentation.request.RefreshRequest;
import com.example.demo.domain.user.presentation.response.RefreshResponse;
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


@Tag(name = "토큰 API")
public interface RefreshControllerDocs {


    @Operation(
            summary = "AccessToken 재발급 API",
            description =
                    """
                    ## AccessToken 재발급

                    accessToken 만료 시 저장된 refreshToken을 이용하여
                    새로운 accessToken을 발급받습니다.

                    ### 처리 방식
                    - refreshToken JWT 검증
                    - refreshToken 만료 여부 확인
                    - 서버에 저장된 refreshToken과 일치 여부 확인
                    - 탈퇴 회원 여부 확인
                    - 새로운 accessToken 발급

                    ### 인증 필요 여부
                    - Authorization Header 불필요
                    - Request Body의 refreshToken 검증 필요
                    """
    )
    @RequestBody(
            required = true,
            content =
            @Content(
                    mediaType = "application/json",
                    examples =
                    @ExampleObject(
                            name = "RefreshToken 요청",
                            value =
                                    """
                                    {
                                      "refreshToken": "eyJhbGciOi..."
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "AccessToken 재발급 성공",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema =
                            @Schema(
                                    implementation = ApiResponseBody.class
                            ),
                            examples =
                            @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "SUCCESS",
                                              "success": {
                                                "data": {
                                                  "accessToken": "eyJhbGciOi..."
                                                },
                                                "error": null,
                                                "meta": {
                                                  "timestamp": "2026-06-30T22:10:00",
                                                  "path": "/api/v1/auth/refresh"
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            ),


            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 RefreshToken",
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
                                                "code": "INVALID_REFRESH_TOKEN",
                                                "message": "유효하지 않은 리프레시 토큰입니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/refresh"
                                              }
                                            }
                                            """
                            )
                    )
            ),


            @ApiResponse(
                    responseCode = "401",
                    description = "만료된 RefreshToken",
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
                                                "code": "EXPIRED_REFRESH_TOKEN",
                                                "message": "만료된 리프레시 토큰입니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/refresh"
                                              }
                                            }
                                            """
                            )
                    )
            ),


            @ApiResponse(
                    responseCode = "401",
                    description = "저장된 RefreshToken 없음",
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
                                                "code": "REFRESH_TOKEN_NOT_FOUND",
                                                "message": "저장된 리프레시 토큰을 찾을 수 없습니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/refresh"
                                              }
                                            }
                                            """
                            )
                    )
            ),


            @ApiResponse(
                    responseCode = "401",
                    description = "탈퇴한 사용자",
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
                                                "code": "WITHDRAWAL_USER",
                                                "message": "탈퇴한 사용자입니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/refresh"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    ApiResponseBody<RefreshResponse> refresh(
            RefreshRequest request,
            HttpServletRequest httpRequest
    );
}