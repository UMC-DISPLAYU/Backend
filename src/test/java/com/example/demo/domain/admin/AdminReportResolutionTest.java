package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ReportResolutionPort;
import com.example.demo.domain.admin.application.result.ReportResolutionResult;
import com.example.demo.domain.admin.application.service.AdminReportResolutionService;
import com.example.demo.domain.admin.infrastructure.config.AdminReportResolutionConfiguration;
import com.example.demo.domain.admin.presentation.AdminReportResolutionController;
import com.example.demo.domain.admin.presentation.mapper.AdminReportResolutionMapper;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminReportResolutionTest {
  private static final String PATH = "/api/v1/admin/report-cases/{reportCaseId}/resolve";
  private static final Instant PROCESSED_AT = Instant.parse("2026-11-05T13:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ReportResolutionPort reports = mock(ReportResolutionPort.class);
  private final AdminReportResolutionService service =
      new AdminReportResolutionService(access, reports);
  private AuthUser operator = new AuthUser(7L);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminReportResolutionController(
                    service, Mappers.getMapper(AdminReportResolutionMapper.class)))
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
                    return operator;
                  }
                })
            .build();
  }

  @ParameterizedTest
  @ValueSource(strings = {"NO_ACTION", "DELETE_COMMENT"})
  void resolvesUsingCaseAndAuthenticatedOperator(String type) throws Exception {
    when(reports.resolve(10L, type, 7L, "운영 검토"))
        .thenReturn(
            new ReportResolutionResult(
                10L, "RESOLVED", type, "COMMENT_KIND", 401L, 7L, PROCESSED_AT));
    mvc.perform(
            post(PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"action\":\""
                        + type
                        + "\",\"reason\":\"  운영 검토  \",\"operatorId\":999,\"reportCaseId\":99,\"commentId\":99}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.action").value(type))
        .andExpect(jsonPath("$.success.data.reportCaseId").value(10))
        .andExpect(jsonPath("$.success.data.status").value("RESOLVED"))
        .andExpect(jsonPath("$.success.data.commentType").value("COMMENT_KIND"))
        .andExpect(jsonPath("$.success.data.commentId").value(401))
        .andExpect(jsonPath("$.success.data.operatorId").value(7))
        .andExpect(jsonPath("$.success.data.processedAt").value(PROCESSED_AT.toString()))
        .andExpect(jsonPath("$.error").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/report-cases/10/resolve"));
    verify(reports).resolve(10L, type, 7L, "운영 검토");
    verifyNoMoreInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"SUSPEND_USER", "DELETE_PARENT", "LOUNGE", "no_action", ""})
  void rejectsUnsupportedActions(String action) throws Exception {
    mvc.perform(
            post(PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"" + action + "\",\"reason\":\"사유\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"action\":\"NO_ACTION\",\"reason\":null}",
        "{\"action\":\"NO_ACTION\",\"reason\":\"\"}",
        "{\"action\":\"NO_ACTION\",\"reason\":\"   \"}",
        "{\"action\":\"NO_ACTION\",\"reason\":\"\\t\\n\"}"
      })
  void rejectsMissingOrBlankReason(String body) throws Exception {
    mvc.perform(post(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(reports);
  }

  @Test
  void validatesReasonLengthAfterStrippingWhitespace() throws Exception {
    for (int length : new int[] {1, 1000}) {
      String reason = "가".repeat(length);
      when(reports.resolve(10L, "NO_ACTION", 7L, reason))
          .thenReturn(
              new ReportResolutionResult(
                  10L, "RESOLVED", "NO_ACTION", "COMMENT_KIND", 401L, 7L, PROCESSED_AT));
      mvc.perform(
              post(PATH, 10)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body("  " + reason + "  ")))
          .andExpect(status().isOk());
      verify(reports).resolve(10L, "NO_ACTION", 7L, reason);
    }
    mvc.perform(
            post(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body("가".repeat(1001))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoMoreInteractions(reports);
  }

  @ParameterizedTest
  @CsvSource({"0,INVALID_INPUT_VALUE", "-1,INVALID_INPUT_VALUE", "abc,INVALID_TYPE_VALUE"})
  void rejectsInvalidCaseId(String id, String code) throws Exception {
    mvc.perform(post(PATH, id).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value(code));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "null", "{", "{\"reason\":[]}"})
  void rejectsMissingOrMalformedJson(String body) throws Exception {
    mvc.perform(post(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("MALFORMED_JSON"));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"GET", "DELETE", "PATCH"})
  void onlyPostCanResolve(String method) throws Exception {
    mvc.perform(
            request(HttpMethod.valueOf(method), PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유")))
        .andExpect(status().isMethodNotAllowed());
    verifyNoInteractions(reports);
  }

  @Test
  void controllerRejectsMissingPrincipal() throws Exception {
    operator = null;
    mvc.perform(post(PATH, 10).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    verifyNoInteractions(reports);
  }

  @Test
  void serviceRejectsOrdinaryOperatorBeforeProcessingInput() {
    assertThatThrownBy(() -> service.resolve(9L, null, null, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(reports);
  }

  @Test
  void serviceRejectsNullActionAndId() {
    assertThatThrownBy(() -> service.resolve(7L, null, 10L, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.resolve(7L, "NO_ACTION", null, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @CsvSource({
    "NO_ACTION,NOT_FOUND,404",
    "DELETE_COMMENT,NOT_FOUND,404",
    "NO_ACTION,CONFLICT,409",
    "DELETE_COMMENT,CONFLICT,409",
    "NO_ACTION,INTERNAL_SERVER_ERROR,500"
  })
  void preservesDomainErrorsWithoutRetrying(String type, String code, int httpStatus)
      throws Exception {
    when(reports.resolve(10L, type, 7L, "사유"))
        .thenThrow(new BusinessException(GlobalErrorCode.valueOf(code)));
    mvc.perform(
            post(PATH, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유").replace("NO_ACTION", type)))
        .andExpect(status().is(httpStatus))
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value(code));
    verify(reports).resolve(10L, type, 7L, "사유");
    verifyNoMoreInteractions(reports);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = "false")
  void featureIsDisabledUnlessExplicitlyEnabled(String enabled) {
    ApplicationContextRunner runner =
        contextRunner()
            .withPropertyValues(
                "admin.report-cases.enabled=true", "admin.content-status.enabled=true");
    if (enabled != null)
      runner = runner.withPropertyValues("admin.report-resolution.enabled=" + enabled);
    runner.run(
        context -> {
          assertThat(context).hasNotFailed();
          assertThat(context).doesNotHaveBean(AdminReportResolutionController.class);
          assertThat(context).doesNotHaveBean(AdminReportResolutionService.class);
        });
  }

  @Test
  void enabledFeatureFailsWithoutResolutionPort() {
    contextRunner()
        .withPropertyValues("admin.report-resolution.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void enabledFeatureRegistersControllerAndServiceWithPorts() {
    contextRunner()
        .withPropertyValues("admin.report-resolution.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ReportResolutionPort.class, () -> reports)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(AdminReportResolutionController.class);
              assertThat(context).hasSingleBean(AdminReportResolutionService.class);
            });
  }

  private ApplicationContextRunner contextRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(
            AdminReportResolutionConfiguration.class, AdminReportResolutionController.class)
        .withBean(
            AdminReportResolutionMapper.class,
            () -> Mappers.getMapper(AdminReportResolutionMapper.class));
  }

  private String body(String reason) {
    return "{\"action\":\"NO_ACTION\",\"reason\":\"" + reason + "\"}";
  }
}
