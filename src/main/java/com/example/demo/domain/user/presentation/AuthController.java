package com.example.demo.domain.user.presentation;


import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.LoginResponseMapper;
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.domain.vo.Nickname;

import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.presentation.docs.AuthControllerDocs;
import com.example.demo.domain.user.presentation.docs.LoginControllerDocs;
import com.example.demo.domain.user.presentation.docs.LogoutControllerDocs;
import com.example.demo.domain.user.presentation.docs.RefreshControllerDocs;

import com.example.demo.domain.user.presentation.request.LogoutRequest;
import com.example.demo.domain.user.presentation.request.RefreshRequest;
import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.request.SocialLoginRequest;

import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.domain.user.presentation.response.SignupResponse;

import com.example.demo.global.error.BusinessException;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.TokenProvider;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements
        AuthControllerDocs,
        LoginControllerDocs,
        RefreshControllerDocs,
        LogoutControllerDocs {


    private final UserService userService;
    private final AuthService authService;

    private final SignupResponseMapper signupResponseMapper;
    private final LoginResponseMapper loginResponseMapper;

    private final TokenProvider tokenProvider;



    /**
     * 회원가입
     * signupToken 필요
     */
    @Override
    @PostMapping("/signup")
    @SecurityRequirement(name = "Authorization")
    public ApiResponseBody<SignupResponse.Signup> signup(

            @Valid
            @RequestBody
            SignupRequest request,

            @RequestHeader("Authorization")
            String authorization,

            HttpServletRequest httpRequest

    ) {


        String signupToken =
                extractBearerToken(
                        authorization
                );


        SocialUserInfo socialUserInfo =
                tokenProvider.parseSignupToken(
                        signupToken
                );



        List<AgreementCommand> agreements =
                request.agreements()
                        .stream()
                        .map(
                                agreement ->
                                        new AgreementCommand(
                                                agreement.agreeId(),
                                                agreement.isAgreed()
                                        )
                        )
                        .toList();



        SignupCommand command =
                new SignupCommand(
                        Nickname.of(
                                request.nickname()
                        ),
                        agreements
                );



        SignupResult result =
                userService.signup(
                        command,
                        socialUserInfo
                );



        SignupResponse.Signup response =
                signupResponseMapper.toResponse(
                        result.user(),
                        result.accessToken(),
                        result.refreshToken()
                );



        return ApiResponseBody.success(
                response,
                httpRequest
        );
    }





    /**
     * 소셜 로그인
     * 인증 필요 없음
     */
    @Override
    @PostMapping("/login")
    public ApiResponseBody<?> login(

            @RequestBody
            SocialLoginRequest request,

            HttpServletRequest httpRequest

    ) {


        LoginResult result =
                authService.login(
                        request
                );


        Object response;


        if (result.user() != null) {

            response =
                    loginResponseMapper.toLoginResponse(
                            result
                    );

        } else {

            response =
                    loginResponseMapper.toSignupResponse(
                            result
                    );
        }



        return ApiResponseBody.success(
                response,
                httpRequest
        );
    }





    /**
     * AccessToken 재발급
     * 인증 필요 없음
     */
    @Override
    @PostMapping("/refresh")
    public ApiResponseBody<RefreshResponse> refresh(

            @RequestBody
            RefreshRequest request,

            HttpServletRequest httpRequest

    ) {


        String accessToken =
                authService.refresh(
                        request.refreshToken()
                );



        RefreshResponse response =
                new RefreshResponse(
                        accessToken
                );



        return ApiResponseBody.success(
                response,
                httpRequest
        );
    }





    /**
     * 로그아웃
     * AccessToken 인증 필요
     */
    @Override
    @PostMapping("/logout")
    @SecurityRequirement(name = "Authorization")
    public ApiResponseBody<Void> logout(

            @RequestBody
            LogoutRequest request,

            HttpServletRequest httpRequest

    ) {


        authService.logout(
                request,
                httpRequest
        );


        return ApiResponseBody.success(
                null,
                httpRequest
        );
    }





    private String extractBearerToken(
            String authorization
    ) {


        if (
                authorization == null ||
                        !authorization.startsWith("Bearer ")
        ) {

            throw new BusinessException(
                    AuthErrorCode.INVALID_SIGNUP_TOKEN
            );
        }


        return authorization.substring(7);
    }

}