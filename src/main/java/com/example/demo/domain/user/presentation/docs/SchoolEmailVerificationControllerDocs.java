package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Tag(name = "User", description = "사용자 인증 API")
public interface SchoolEmailVerificationControllerDocs {

    @Operation(
            summary = "학교 이메일 인증번호 발송",
            description =
                    """
                    작가 인증이 필요한 기능을 사용하기 위해 학교 이메일 인증번호를 발송합니다.
          
                    - 학교 이메일 형식을 검증합니다.
                    - UnivCert API를 통해 학교 존재 여부를 확인합니다.
                    - 입력한 학교 이메일로 인증번호를 발송합니다.
          
                    인증번호 확인 완료 후 작가 인증이 완료됩니다.
                    """)
    @SecurityRequirement(name = "Authorization")
    ApiResponseBody<Void> send(
            SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest
    ) throws IOException;
}