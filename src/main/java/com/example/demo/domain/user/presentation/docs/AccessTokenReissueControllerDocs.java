package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.RefreshResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "사용자 인증 API")
public interface AccessTokenReissueControllerDocs {

  @Operation(
      summary = "Access Token 재발급",
      description =
          """
          HttpOnly Cookie의 Refresh Token을 검증하여 새로운 Access Token을 발급합니다.
          응답의 Access Token을 Swagger Authorize에 입력하면 인증 API를 테스트할 수 있습니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "Access Token 재발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Access Token 재발급 응답",
                      value =
                          """
                              {"accessToken":"eyJhbGciOi..."}
                              """)))
  RefreshResponse reissue(@Parameter(hidden = true) String refreshToken);
}
