package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.presentation.docs.SchoolEmailVerificationControllerDocs;
import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/verification/email")
public class SchoolEmailVerificationController
        implements SchoolEmailVerificationControllerDocs {

    private final SendSchoolEmailVerificationService sendService;
    private final ResendSchoolEmailVerificationService resendService;


    @Override
    @PostMapping("/send")
    public ApiResponseBody<Void> send(
            @RequestBody SchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest) {

        sendService.execute(
                new SendSchoolEmailVerificationCommand(
                        request.schoolEmail(),
                        request.univName()
                )
        );

        return ApiResponseBody.success(
                null,
                httpRequest
        );
    }


    @Override
    @PostMapping("/resend")
    public ApiResponseBody<Void> resend(
            @RequestBody ResendSchoolEmailVerificationRequest request,
            HttpServletRequest httpRequest) {

        resendService.execute(
                request.schoolEmail()
        );

        return ApiResponseBody.success(
                null,
                httpRequest
        );
    }
}