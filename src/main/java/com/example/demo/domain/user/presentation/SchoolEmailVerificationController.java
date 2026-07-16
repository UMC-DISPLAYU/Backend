package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.mapper.SchoolEmailVerificationMapper;
import com.example.demo.domain.user.application.result.SchoolEmailConfirmVerificationResult;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.VerifySchoolEmailVerificationService;
import com.example.demo.domain.user.presentation.docs.SchoolEmailVerificationControllerDocs;
import com.example.demo.domain.user.presentation.request.ResendSchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.SchoolEmailVerificationRequest;
import com.example.demo.domain.user.presentation.request.VerifySchoolEmailRequest;
import com.example.demo.domain.user.presentation.response.SchoolEmailVerificationConfirmResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final SchoolEmailVerificationMapper mapper;

  @Override
  @PostMapping("/send")
  public ApiResponseBody<Void> send(
      @RequestBody SchoolEmailVerificationRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    sendService.execute(
        new SendSchoolEmailVerificationCommand(user.userId(), request.schoolEmail(), request.univName()));
    return ApiResponseBody.success(null, httpRequest);
  }

  @Override
  @PostMapping("/resend")
  public ApiResponseBody<Void> resend(
      @RequestBody ResendSchoolEmailVerificationRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    resendService.execute(user.userId(), request.schoolEmail());
    return ApiResponseBody.success(null, httpRequest);
  }

  @Override
  @PostMapping("/confirm")
  public ApiResponseBody<SchoolEmailVerificationConfirmResponse> confirm(
      @RequestBody VerifySchoolEmailRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    SchoolEmailConfirmVerificationResult result =
        verifyService.execute(
            new VerifySchoolEmailVerificationCommand(
                user.userId(), request.schoolEmail(), request.verificationCode()));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }
}
