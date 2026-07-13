package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.LogoutRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface LogoutControllerDocs {

    @Operation(
            summary = "로그아웃",
            description =
                    """
                    현재 로그인된 사용자를 로그아웃 처리합니다.
          
                    - Access Token으로 사용자를 인증합니다.
                    - Refresh Token 검증 후 저장된 Refresh Token을 삭제합니다.
                    - 로그아웃 완료 후 기존 Refresh Token은 사용할 수 없습니다.
                    """)
    ApiResponseBody<Void> logout(
            LogoutRequest request,
            HttpServletRequest httpRequest);
}