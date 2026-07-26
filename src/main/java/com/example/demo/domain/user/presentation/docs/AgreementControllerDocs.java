package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.AgreementResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
          회원가입 전에 현재 DB에 저장된 약관 목록을 조회합니다.
          회원가입에 사용하는 위치 기반 서비스 약관, 서비스 이용약관, 개인정보 처리방침,
          마케팅 정보 수신 동의만 반환합니다.
          agreementId는 환경마다 다릅니다.
          프론트엔드는 약관 ID를 하드코딩하지 않고 응답받은 agreementId를 회원가입 요청에 사용해야 합니다.
          위치 기반 서비스 약관, 서비스 이용약관, 개인정보 처리방침은 필수이고
          마케팅 정보 수신 동의는 선택입니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "약관 목록 조회 성공",
      content = @Content(mediaType = "application/json"))
  ApiResponseBody<List<AgreementResponse>> getAgreements(HttpServletRequest request);
}
