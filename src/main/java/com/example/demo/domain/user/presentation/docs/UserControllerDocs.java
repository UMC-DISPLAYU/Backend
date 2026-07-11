package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "회원 API")
public interface UserControllerDocs {

    @Operation(
            summary = "닉네임 중복 확인 API",
            description =
                    """
                    ## 닉네임 중복 확인

                    회원가입 또는 닉네임 변경 과정에서 사용자가 '중복 확인' 버튼을 눌렀을 때 호출하는 API입니다.
                    입력한 닉네임이 이미 사용 중인지 확인하고, 사용 가능 여부를 반환합니다.

                    - 닉네임 중복 여부는 정상 조회 결과로 간주하여 항상 SUCCESS로 응답합니다.
                    - 형식이 올바르지 않거나 nickname 파라미터가 없는 경우에만 실패 응답을 반환합니다.
                    """)
    @Parameter(
            name = "nickname",
            description = "중복 확인할 닉네임",
            required = true,
            example = "User1"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "닉네임 중복 확인 성공 (사용 가능/불가능 모두 포함)",
                    content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseBody.class),
                            examples = {
                                    @ExampleObject(
                                            name = "사용 가능",
                                            value =
                                                    """
                                                    {
                                                      "resultType": "SUCCESS",
                                                      "success": {
                                                        "data": {
                                                          "nickname": "User1",
                                                          "isAvailable": true
                                                        },
                                                        "error": null,
                                                        "meta": {
                                                          "timestamp": "2026-06-30T22:10:00",
                                                          "path": "/api/v1/users/nickname/check"
                                                        }
                                                      }
                                                    }
                                                    """),
                                    @ExampleObject(
                                            name = "사용 불가능(중복)",
                                            value =
                                                    """
                                                    {
                                                      "resultType": "SUCCESS",
                                                      "success": {
                                                        "data": {
                                                          "nickname": "User1",
                                                          "isAvailable": false
                                                        },
                                                        "error": null,
                                                        "meta": {
                                                          "timestamp": "2026-06-30T22:10:00",
                                                          "path": "/api/v1/users/nickname/check"
                                                        }
                                                      }
                                                    }
                                                    """)
                            })),

            @ApiResponse(
                    responseCode = "400",
                    description = "닉네임 형식이 올바르지 않음",
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
                                                "code": "INVALID_NICKNAME_FORMAT",
                                                "message": "닉네임은 한글, 영문, 숫자로 5~15자여야 하며 공백과 특수문자는 사용할 수 없습니다."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/nickname/check"
                                              }
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "400",
                    description = "nickname 쿼리 파라미터 누락",
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
                                                "code": "MISSING_NICKNAME",
                                                "message": "닉네임을 입력해주세요."
                                              },
                                              "meta": {
                                                "timestamp": "2026-06-30T22:10:00",
                                                "path": "/api/v1/users/nickname/check"
                                              }
                                            }
                                            """)))
    })
    ApiResponseBody<NicknameCheckResponse> checkNickname(
            String nickname,
            HttpServletRequest httpRequest
    );
}git