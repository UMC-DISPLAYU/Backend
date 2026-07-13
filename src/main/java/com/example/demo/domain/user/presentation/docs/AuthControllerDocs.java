package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface AuthControllerDocs {

    @Operation(
            summary = "회원가입",
            description =
                    """
                    OAuth 인증이 완료된 사용자가 회원가입을 진행합니다.
          
                    - signupToken을 통해 인증된 사용자임을 확인합니다.
                    - 닉네임과 약관 동의 정보를 저장합니다.
                    - 회원가입 완료 후 사용자 정보를 반환합니다.
                    """)
    @SecurityRequirement(name = "Authorization")
    ApiResponseBody<SignupResponse.Signup> signup(
            SignupRequest request,
            String authorization,
            HttpServletRequest httpRequest);
}