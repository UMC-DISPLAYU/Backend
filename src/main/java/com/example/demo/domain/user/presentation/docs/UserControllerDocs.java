package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface UserControllerDocs {

    @Operation(
            summary = "닉네임 중복 확인",
            description =
                    """
                    회원가입 또는 닉네임 변경 과정에서 닉네임 사용 가능 여부를 확인합니다.

                    - 닉네임이 사용 가능하면 isAvailable=true를 반환합니다.
                    - 이미 사용 중인 닉네임이면 isAvailable=false를 반환합니다.
                    - 닉네임 형식이 올바르지 않은 경우 실패 응답을 반환합니다.
                    """)
    @Parameter(
            name = "nickname",
            description = "중복 확인할 닉네임",
            required = true,
            example = "maya041225")
    ApiResponseBody<NicknameCheckResponse> checkNickname(
            String nickname,
            HttpServletRequest httpRequest);



    @Operation(
            summary = "학교 이메일 인증번호 발송",
            description =
                    """
                    작가 인증을 위해 학교 이메일로 인증번호를 발송합니다.

                    - 학교 이메일 형식을 검증합니다.
                    - 입력한 학교 이메일로 인증번호를 발송합니다.
                    - 인증 완료 처리는 인증번호 확인 API에서 진행합니다.
                    """)
    ApiResponseBody<Void> sendSchoolEmailVerification(
            SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest);
}