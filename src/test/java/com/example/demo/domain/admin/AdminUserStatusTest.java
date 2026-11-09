package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.UserStatusPort;
import com.example.demo.domain.admin.application.result.UserStatusResult;
import com.example.demo.domain.admin.application.service.AdminUserStatusService;
import com.example.demo.domain.admin.infrastructure.config.AdminUserStatusConfiguration;
import com.example.demo.domain.admin.presentation.AdminUserStatusController;
import com.example.demo.domain.admin.presentation.mapper.AdminUserStatusMapper;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.AuthUser;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminUserStatusTest {
  private static final String PATH = "/api/v1/admin/users/{userId}/account-status";
  private static final Instant PROCESSED_AT = Instant.parse("2026-11-05T09:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final UserStatusPort users = mock(UserStatusPort.class);
  private final AdminUserStatusService service = new AdminUserStatusService(access, users);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminUserStatusController(
                    service, Mappers.getMapper(AdminUserStatusMapper.class)))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(
                new HandlerMethodArgumentResolver() {
                  public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.getParameterType() == AuthUser.class;
                  }

                  public Object resolveArgument(
                      MethodParameter parameter,
                      ModelAndViewContainer container,
                      NativeWebRequest request,
                      WebDataBinderFactory factory) {
                    return new AuthUser(7L);
                  }
                })
            .build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"SUSPENDED", "ACTIVE"})
  void changesStatusWithAuthenticatedOperatorAndNormalizedReason(String status) throws Exception {
    when(users.changeStatus(10L, 7L, status, "운영 검토"))
        .thenReturn(new UserStatusResult(10L, status, PROCESSED_AT));

    mvc.perform(
            patch(PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"accountStatus":"%s","reason":"  운영 검토  ","operatorId":999}
                    """
                        .formatted(status)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.userId").value(10))
        .andExpect(jsonPath("$.success.data.accountStatus").value(status))
        .andExpect(jsonPath("$.success.data.processedAt").value(PROCESSED_AT.toString()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/users/10/account-status"));
    verify(users).changeStatus(10L, 7L, status, "운영 검토");
    verifyNoMoreInteractions(users);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"reason\":\"사유\"}",
        "{\"accountStatus\":null,\"reason\":\"사유\"}",
        "{\"accountStatus\":\"suspended\",\"reason\":\"사유\"}",
        "{\"accountStatus\":\"DELETED\",\"reason\":\"사유\"}",
        "{\"accountStatus\":\" ACTIVE \",\"reason\":\"사유\"}",
        "{\"accountStatus\":\"SUSPENDED\"}",
        "{\"accountStatus\":\"SUSPENDED\",\"reason\":null}",
        "{\"accountStatus\":\"ACTIVE\",\"reason\":\"\"}",
        "{\"accountStatus\":\"ACTIVE\",\"reason\":\"   \"}"
      })
  void rejectsInvalidBodyWithoutCallingUserPort(String body) throws Exception {
    mvc.perform(patch(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(users);
  }

  @Test
  void acceptsOneAndThousandCharacterReasonsAfterStrippingWhitespace() throws Exception {
    for (int length : new int[] {1, 1000}) {
      String reason = "가".repeat(length);
      when(users.changeStatus(10L, 7L, "ACTIVE", reason))
          .thenReturn(new UserStatusResult(10L, "ACTIVE", PROCESSED_AT));
      mvc.perform(
              patch(PATH, 10)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"accountStatus\":\"ACTIVE\",\"reason\":\"  " + reason + "  \"}"))
          .andExpect(status().isOk());
      verify(users).changeStatus(10L, 7L, "ACTIVE", reason);
    }
  }

  @Test
  void rejectsReasonOverThousandCharacters() throws Exception {
    mvc.perform(
            patch(PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"accountStatus\":\"SUSPENDED\",\"reason\":\"" + "가".repeat(1001) + "\"}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @CsvSource({"0,INVALID_INPUT_VALUE", "-1,INVALID_INPUT_VALUE", "abc,INVALID_TYPE_VALUE"})
  void rejectsInvalidTargetId(String id, String code) throws Exception {
    mvc.perform(patch(PATH, id).contentType(MediaType.APPLICATION_JSON).content(validBody()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value(code));
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "null", "{", "{\"accountStatus\":[]}"})
  void rejectsMissingOrMalformedJson(String body) throws Exception {
    mvc.perform(patch(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("MALFORMED_JSON"));
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @ValueSource(longs = {7L, 8L})
  void rejectsSelfAndOtherAdministrators(Long targetId) throws Exception {
    when(access.isAdmin(8L)).thenReturn(true);
    mvc.perform(patch(PATH, targetId).contentType(MediaType.APPLICATION_JSON).content(validBody()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    verifyNoInteractions(users);
  }

  @Test
  void serviceRejectsAnonymousAndOrdinaryOperators() {
    assertThatThrownBy(() -> service.changeStatus(null, 10L, "ACTIVE", "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.UNAUTHORIZED));
    assertThatThrownBy(() -> service.changeStatus(9L, 10L, "ACTIVE", "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(users);
  }

  @Test
  void serviceRejectsNullTargetId() {
    assertThatThrownBy(() -> service.changeStatus(7L, null, "ACTIVE", "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @CsvSource({"NOT_FOUND,404", "FORBIDDEN,403", "CONFLICT,409"})
  void preservesUserPortErrorsWithoutRetrying(String code, int httpStatus) throws Exception {
    when(users.changeStatus(10L, 7L, "SUSPENDED", "사유"))
        .thenThrow(new BusinessException(GlobalErrorCode.valueOf(code)));
    mvc.perform(patch(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(validBody()))
        .andExpect(status().is(httpStatus))
        .andExpect(jsonPath("$.error.code").value(code));
    verify(users).changeStatus(10L, 7L, "SUSPENDED", "사유");
    verifyNoMoreInteractions(users);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = "false")
  void featureIsDisabledUnlessExplicitlyEnabled(String enabled) {
    ApplicationContextRunner runner = contextRunner();
    if (enabled != null) runner = runner.withPropertyValues("admin.user-status.enabled=" + enabled);
    runner.run(
        context -> {
          assertThat(context).hasNotFailed();
          assertThat(context).doesNotHaveBean(AdminUserStatusController.class);
          assertThat(context).doesNotHaveBean(AdminUserStatusService.class);
        });
  }

  @Test
  void enabledFeatureRequiresRealUserPort() {
    contextRunner()
        .withPropertyValues("admin.user-status.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void enabledFeatureRegistersControllerAndServiceWithPorts() {
    contextRunner()
        .withPropertyValues("admin.user-status.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(UserStatusPort.class, () -> users)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(AdminUserStatusController.class);
              assertThat(context).hasSingleBean(AdminUserStatusService.class);
            });
  }

  private ApplicationContextRunner contextRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(AdminUserStatusConfiguration.class, AdminUserStatusController.class)
        .withBean(
            AdminUserStatusMapper.class, () -> Mappers.getMapper(AdminUserStatusMapper.class));
  }

  private String validBody() {
    return "{\"accountStatus\":\"SUSPENDED\",\"reason\":\"사유\"}";
  }
}
