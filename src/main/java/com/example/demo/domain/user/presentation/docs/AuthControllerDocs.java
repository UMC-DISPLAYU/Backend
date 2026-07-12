package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.response.ApiResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;


public interface AuthControllerDocs {


    @Operation(
            summary = "회원가입 API",
            description =
                    """
                    ## 회원가입

                    OAuth 인증이 완료된 사용자가 닉네임과 약관 동의를 입력하여 회원가입합니다.

                    ### Header
                    - Authorization: Bearer signupToken

                    ### 요청 사항
                    - nickname : 사용할 닉네임
                    - agreements : 약관 동의 목록
                    """
    )
    @SecurityRequirement(
            name = "Authorization"
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema =
                            @Schema(
                                    implementation = ApiResponseBody.class
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 회원가입 토큰",
                    content =
                    @Content(
                            mediaType = "application/json",
                            examples =
                            @ExampleObject(
                                    value =
                                            """
                                            {
                                              "resultType": "FAIL",
                                              "error": {
                                                "code": "INVALID_SIGNUP_TOKEN",
                                                "message": "유효하지 않은 회원가입 토큰입니다."
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    ApiResponseBody<SignupResponse.Signup> signup(
            SignupRequest request,
            String authorization,
            HttpServletRequest httpRequest
    );

}