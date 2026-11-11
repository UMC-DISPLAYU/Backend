package com.example.demo.domain.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.command.SendSchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.command.VerifySchoolEmailVerificationCommand;
import com.example.demo.domain.user.application.mapper.SchoolEmailVerificationMapper;
import com.example.demo.domain.user.application.result.SchoolEmailConfirmVerificationResult;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.VerifySchoolEmailVerificationService;
import com.example.demo.global.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class SchoolEmailVerificationControllerTest {

  private final SendSchoolEmailVerificationService sendService =
      mock(SendSchoolEmailVerificationService.class);
  private final ResendSchoolEmailVerificationService resendService =
      mock(ResendSchoolEmailVerificationService.class);
  private final VerifySchoolEmailVerificationService verifyService =
      mock(VerifySchoolEmailVerificationService.class);
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    SchoolEmailVerificationController controller =
        new SchoolEmailVerificationController(
            sendService, resendService, verifyService, new SchoolEmailVerificationMapper());
    HandlerMethodArgumentResolver authUserResolver =
        new HandlerMethodArgumentResolver() {
          @Override
          public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType().equals(AuthUser.class);
          }

          @Override
          public Object resolveArgument(
              MethodParameter parameter,
              ModelAndViewContainer mavContainer,
              NativeWebRequest webRequest,
              WebDataBinderFactory binderFactory) {
            return new AuthUser(1L);
          }
        };

    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setCustomArgumentResolvers(authUserResolver)
            .build();
  }

  @Test
  void keepsSendApiRequestAndResponseContract() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/me/verification/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"schoolEmail":"student@dgu.ac.kr","univName":"동국대학교(서울)"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data").doesNotExist());

    verify(sendService)
        .execute(new SendSchoolEmailVerificationCommand(1L, "student@dgu.ac.kr", "동국대학교(서울)"));
  }

  @Test
  void keepsResendApiRequestAndResponseContract() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/users/me/verification/email/resend")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"schoolEmail\":\"student@dgu.ac.kr\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data").doesNotExist());

    verify(resendService).execute(1L, "student@dgu.ac.kr");
  }

  @Test
  void keepsConfirmApiRequestAndResponseContract() throws Exception {
    when(verifyService.execute(any(VerifySchoolEmailVerificationCommand.class)))
        .thenReturn(new SchoolEmailConfirmVerificationResult("student@dgu.ac.kr", true));

    mockMvc
        .perform(
            post("/api/v1/users/me/verification/email/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"schoolEmail":"student@dgu.ac.kr","verificationCode":"123456"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.schoolEmail").value("student@dgu.ac.kr"))
        .andExpect(jsonPath("$.success.data.isVerified").value(true));

    verify(verifyService)
        .execute(new VerifySchoolEmailVerificationCommand(1L, "student@dgu.ac.kr", "123456"));
  }
}
