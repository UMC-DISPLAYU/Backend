package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.LogoutRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;

public interface LogoutControllerDocs {

    @Operation(
            summary = "로그아웃 API",
            description =
                    """
                    ## 로그아웃

                    사용자가 로그아웃 버튼을 눌렀을 때 호출하는 API입니다.

                    ### 처리 방식
                    - Authorization Header의 accessToken으로 사용자를 인증합니다.
                    - 요청 Body의 refreshToken이 해당 사용자에게 발급된 토큰인지 확인합니다.
                    - accessToken의 사용자와 refreshToken의 사용자가 일치하면 서버에 저장된 refreshToken을 삭제합니다.
                    - 삭제가 완료되면 로그인 세션이 종료됩니다.
                    """
    )
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
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
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ApiResponseBody.class
                            ),
                            examples = @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "SUCCESS",
                                              "success": {
                                                "data": null
                                              },
                                              "error": null,
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/auth/logout"
                                              }
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 액세스 토큰"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰"
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "저장된 리프레시 토큰 없음"
            ),

            @ApiResponse(
                    responseCode = "403",
                    description = "토큰 사용자 불일치"
            )
    })
    ApiResponseBody<Void> logout(
            LogoutRequest request,
            HttpServletRequest httpRequest
    );
}