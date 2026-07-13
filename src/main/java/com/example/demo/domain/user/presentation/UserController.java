package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.presentation.docs.UserControllerDocs;
import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final SendSchoolEmailVerificationService sendSchoolEmailVerificationService;
    private final ResendSchoolEmailVerificationService resendSchoolEmailVerificationService;


    @Override
    @GetMapping("/nickname/check")
    public ApiResponseBody<NicknameCheckResponse> checkNickname(
            @RequestParam(value = "nickname", required = false) String nickname,
            HttpServletRequest httpRequest) {

        if (nickname == null || nickname.isBlank()) {
            throw new UserException(UserErrorCode.MISSING_NICKNAME);
        }

        boolean isAvailable =
                userService.isNicknameAvailable(nickname);

        return ApiResponseBody.success(
                new NicknameCheckResponse(nickname, isAvailable),
                httpRequest);
    }


    @PostMapping("/me/verification/email/send")
    public ApiResponseBody<Void> sendSchoolEmailVerification(
            @Valid @RequestBody SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest) {

        sendSchoolEmailVerificationService.execute(
                new SendSchoolEmailVerificationCommand(
                        request.schoolEmail(),
                        request.univName()
                )
        );

        return ApiResponseBody.success(
                null,
                httpRequest);
    }
    @PostMapping("/me/verification/email/resend")
    public ApiResponseBody<Void> resendSchoolEmailVerification(
            @RequestBody ResendSchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest
    ) {

        resendSchoolEmailVerificationService.execute(
                request.schoolEmail()
        );

        return ApiResponseBody.success(
                null,
                httpRequest
        );
    }
}