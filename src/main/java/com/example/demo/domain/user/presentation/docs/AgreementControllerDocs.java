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

@Tag(name = "Agreement", description = "약관 및 정책 문서 API")
public interface AgreementControllerDocs {

  @Operation(
      summary = "약관 및 정책 문서 목록 조회",
      description =
          """
          활성 상태이고 시행일이 도래한 최신 약관 및 정책 문서를 displayOrder 순으로 조회합니다.
          서비스 이용약관만 회원가입 필수 동의이며, 개인정보 수집·이용 안내와 개인정보 처리방침은 조회 전용 문서입니다.
          위치기반서비스 이용약관은 목록에서 제외됩니다.
          회원가입 요청에는 TERMS_OF_SERVICE의 code와 version만 사용해야 합니다.
          실제 API 응답의 content에는 각 문서의 전문 전체가 반환됩니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "약관 및 정책 문서 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "약관 및 정책 문서 목록 (content는 전문 중 일부 예시)",
                      value = AGREEMENT_SUCCESS_EXAMPLE)))
  ApiResponseBody<List<AgreementResponse>> getAgreements(HttpServletRequest request);

  String AGREEMENT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": { "data": [
          {
            "agreementId": 7,
            "code": "TERMS_OF_SERVICE",
            "title": "서비스 이용약관",
            "type": "SERVICE",
            "content": "서비스 이용약관\\n시행일: 2026년 11월 21일\\n제1장 총칙\\n제1조 (목적)\\n이 약관은 디유 운영팀(이하 \\\"운영자\\\")이 제공하는 디유(displayU) 서비스를 이용할 때 운영자와 회원 사이의 권리, 의무, 책임과 이용 절차를 정합니다.",
            "required": true,
            "version": "2.0",
            "effectiveDate": "2026-11-21",
            "displayOrder": 1
          },
          {
            "agreementId": 8,
            "code": "PRIVACY_COLLECTION_USE",
            "title": "개인정보 수집·이용 안내",
            "type": "PRIVACY",
            "content": "개인정보 수집·이용 안내\\n시행일: 2026년 11월 21일\\n디유 운영팀(이하 \\\"운영자\\\")는 디유 가입과 서비스 이용에 필요한 만큼의 개인정보를 아래와 같이 수집하고 이용합니다.",
            "required": false,
            "version": "2.0",
            "effectiveDate": "2026-11-21",
            "displayOrder": 2
          },
          {
            "agreementId": 9,
            "code": "PRIVACY_POLICY",
            "title": "개인정보 처리방침",
            "type": "PRIVACY",
            "content": "개인정보 처리방침\\n시행일: 2026년 11월 21일\\n디유 운영팀(이하 \\\"운영자\\\")은 디유(displayU)를 이용하는 사람의 개인정보를 보호하고, 이와 관련한 고충을 신속하게 처리하기 위해 이 처리방침을 둡니다.",
            "required": false,
            "version": "1.0",
            "effectiveDate": "2026-11-21",
            "displayOrder": 3
          }
        ]},
        "error": null,
        "meta": { "timestamp": "2026-11-21T00:00:00", "path": "/api/v1/agreements" }
      }
      """;
}
