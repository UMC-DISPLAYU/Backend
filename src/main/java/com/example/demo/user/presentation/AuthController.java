package com.example.demo.user.presentation;

import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.user.application.auth.SocialUserInfo;
import com.example.demo.user.application.command.AgreementCommand;
import com.example.demo.user.application.command.SignupCommand;
import com.example.demo.user.application.mapper.SignupResponseMapper;
import com.example.demo.user.application.result.SignupResult;
import com.example.demo.user.application.service.UserService;
import com.example.demo.user.domain.enums.Provider;
import com.example.demo.user.presentation.docs.AuthControllerDocs;
import com.example.demo.user.presentation.request.SignupRequest;
import com.example.demo.user.presentation.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthControllerDocs {

    private final UserService userService;
    private final SignupResponseMapper signupResponseMapper;


    @Override
    @PostMapping("/signup")
    public ApiResponseBody<SignupResponse.Signup> signup(
            @Valid @org.springframework.web.bind.annotation.RequestBody SignupRequest request,
            HttpServletRequest httpRequest
    ) {

        List<AgreementCommand> agreements =
                request.agreements()
                        .stream()
                        .map(agreement ->
                                new AgreementCommand(
                                        agreement.agreeId(),
                                        agreement.isAgreed()
                                )
                        )
                        .toList();


        SignupCommand command =
                new SignupCommand(
                        request.nickname(),
                        agreements
                );


        // TODO: OAuth 인증 후 주입받도록 변경
        SocialUserInfo socialUserInfo =
                new SocialUserInfo(
                        Provider.Kakao,
                        "kakao-0012",
                        "마야",
                        "maya@gmail.com"
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
}