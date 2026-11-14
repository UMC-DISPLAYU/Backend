package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.AgreementResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Tag(name = "Agreement", description = "회원가입 약관 API")
public interface AgreementControllerDocs {

  @Operation(
      summary = "회원가입 약관 목록 조회",
      description =
          """
          활성화된 회원가입 약관을 displayOrder 순으로 조회합니다.
          서비스 이용약관과 개인정보 수집·이용 동의는 필수이고 위치기반서비스 이용약관은 선택입니다.
          프론트엔드는 code를 기준으로 화면을 구성하고 응답받은 code와 version을 회원가입 요청에 사용해야 합니다.
          effectiveDate는 안내 정보이며 시행일 전이라도 isActive=true인 약관은 반환합니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "약관 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "약관 목록", value = AGREEMENT_SUCCESS_EXAMPLE)))
  ApiResponseBody<List<AgreementResponse>> getAgreements(HttpServletRequest request);

  String AGREEMENT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": [
          {
            "agreementId": 1,
            "code": "TERMS_OF_SERVICE",
            "title": "서비스 이용약관",
            "type": "SERVICE",
            "content": "DisplayU 서비스 이용약관 내용입니다.",
            "required": true,
            "version": "1.0",
            "effectiveDate": "2026-01-01",
            "displayOrder": 1
          }
        ]},
        "error": null,
        "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/agreements" }
      }
      """;
}
