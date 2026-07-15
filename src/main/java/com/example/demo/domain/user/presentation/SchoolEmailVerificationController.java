package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.VerifySchoolEmailVerificationService;
import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.presentation.docs.SchoolEmailVerificationControllerDocs;
import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.VerifySchoolEmailRequest;
import com.example.demo.domain.user.presentation.response.SchoolEmailVerificationConfirmResponse;
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
public class SchoolEmailVerificationController implements SchoolEmailVerificationControllerDocs {

  private final SendSchoolEmailVerificationService sendService;
  private final ResendSchoolEmailVerificationService resendService;
  private final VerifySchoolEmailVerificationService verifyService;

  @Override
  @PostMapping("/send")
  public ApiResponseBody<Void> send(
      @RequestBody SchoolEmailVerificationRequest request, HttpServletRequest httpRequest) {

    sendService.execute(
        new SendSchoolEmailVerificationCommand(request.schoolEmail(), request.univName()));

    return ApiResponseBody.success(null, httpRequest);
  }

  @Override
  @PostMapping("/resend")
  public ApiResponseBody<Void> resend(
      @RequestBody ResendSchoolEmailVerificationRequest request, HttpServletRequest httpRequest) {

    resendService.execute(request.schoolEmail());

    return ApiResponseBody.success(null, httpRequest);
  }

  @PostMapping("/confirm")
  public ApiResponseBody<SchoolEmailVerificationConfirmResponse> confirm(
      @RequestBody VerifySchoolEmailRequest request, HttpServletRequest httpRequest) {

    SchoolEmailVerification verification =
        verifyService.execute(
            new VerifySchoolEmailVerificationCommand(
                request.schoolEmail(), request.verificationCode()));

    return ApiResponseBody.success(
        SchoolEmailVerificationConfirmResponse.from(verification), httpRequest);
  }
}
