package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.mapper.LoginResponseMapper;
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.presentation.docs.AuthControllerDocs;
import com.example.demo.domain.user.presentation.docs.LoginControllerDocs;
import com.example.demo.domain.user.presentation.docs.RefreshControllerDocs;
import com.example.demo.domain.user.presentation.request.RefreshRequest;
import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.request.SocialLoginRequest;

import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController
        implements AuthControllerDocs, LoginControllerDocs, RefreshControllerDocs {


    private final UserService userService;
    private final AuthService authService;

    private final SignupResponseMapper signupResponseMapper;
    private final LoginResponseMapper loginResponseMapper;

    private final TokenProvider tokenProvider;


    @Override
    @PostMapping("/signup")
    public ApiResponseBody<SignupResponse.Signup> signup(
            @Valid @RequestBody SignupRequest request,
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest httpRequest
    ) {


        List<AgreementCommand> agreements =
                request.agreements()
                        .stream()
                        .map(agreement ->
                                new AgreementCommand(
                                        agreement.agreeId(),
                                        agreement.isAgreed()
                                ))
                        .toList();


        SignupCommand command =
                new SignupCommand(
                        request.nickname(),
                        agreements
                );


        String signupToken =
                authorization.replace("Bearer ", "");


        SocialUserInfo socialUserInfo =
                tokenProvider.parseSignupToken(
                        signupToken
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


    @Override
    @PostMapping("/login")
    public ApiResponseBody<?> login(
            @RequestBody SocialLoginRequest request,
            HttpServletRequest httpRequest
    ) {


        LoginResult result =
                authService.login(request);


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


    @PostMapping("/refresh")
    public ApiResponseBody<RefreshResponse> refresh(
            @RequestBody RefreshRequest request,
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
}