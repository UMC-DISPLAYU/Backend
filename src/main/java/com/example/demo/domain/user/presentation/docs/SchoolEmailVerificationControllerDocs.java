package com.example.demo.domain.user.presentation.docs;


import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.global.response.ApiResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import java.io.IOException;


public interface SchoolEmailVerificationControllerDocs {


    @Operation(
            summary = "학교 이메일 인증번호 발송 API",
            description =
                    """
                    ## 학교 이메일 인증번호 발송

                    작가 인증이 필요한 기능을 사용하려는 사용자가
                    학교 이메일을 입력하고 인증번호 받기를 요청합니다.

                    ### 처리 방식
                    - 학교 이메일 형식 검증
                    - UnivCert API를 통한 학교 존재 여부 확인
                    - 입력한 학교 이메일로 인증번호 발송

                    ### 인증 필요 여부
                    - Authorization Header 필요
                    - accessToken으로 로그인 사용자 확인

                    ### 참고
                    - 본 API는 인증번호 발송만 수행합니다.
                    - 작가 인증 완료 처리는 인증번호 확인 API에서 진행합니다.
                    """
    )
    @RequestBody(
            required = true,
            content =
            @Content(
                    mediaType = "application/json",
                    examples =
                    @ExampleObject(
                            name = "학교 이메일 인증 요청",
                            value =
                                    """
                                    {
                                      "schoolEmail": "user@university.ac.kr",
                                      "univName": "ㅇㅇ대학교"
                                    }
                                    """
                    )
            )
    )
    @ApiResponses({

            @ApiResponse(
                    responseCode = "200",
                    description = "인증번호 발송 성공",
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
                                                "data": null
                                              },
                                              "error": null,
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/me/verification/email/send"
                                              }
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 학교 이메일",
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
                                                "code": "INVALID_EMAIL",
                                                "message": "유효하지 않은 학교 이메일입니다.",
                                                "details": null
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/me/verification/email/send"
                                              }
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "지원하지 않는 대학교",
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
                                                "code": "UNSUPPORTED_UNIVERSITY",
                                                "message": "지원하지 않는 대학교입니다.",
                                                "details": null
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/me/verification/email/send"
                                              }
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "이미 작가 인증 완료",
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
                                                "code": "ALREADY_VERIFIED_USER",
                                                "message": "이미 작가 인증이 완료된 사용자입니다.",
                                                "details": null
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/me/verification/email/send"
                                              }
                                            }
                                            """
                            )
                    )
            ),

            @ApiResponse(
                    responseCode = "500",
                    description = "이메일 발송 실패",
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
                                                "code": "EMAIL_SEND_FAILED",
                                                "message": "인증 이메일 발송에 실패했습니다.",
                                                "details": null
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/me/verification/email/send"
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponseBody<Void>> send(
            SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest
    ) throws IOException;
}