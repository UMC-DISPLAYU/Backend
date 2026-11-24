package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.OperationLogPort;
import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.domain.admin.application.result.OperationLogPage;
import com.example.demo.domain.admin.application.service.AdminOperationLogService;
import com.example.demo.domain.admin.infrastructure.config.AdminOperationLogConfiguration;
import com.example.demo.domain.admin.presentation.AdminOperationLogController;
import com.example.demo.domain.admin.presentation.mapper.AdminOperationLogMapper;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.AuthUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminOperationLogTest {
  private static final String PATH = "/api/v1/admin/operations";
  private static final Instant FROM = Instant.parse("2026-11-01T00:00:00Z");
  private static final Instant TO = Instant.parse("2026-11-02T00:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final OperationLogPort logs = mock(OperationLogPort.class);
  private final AdminOperationLogService service = new AdminOperationLogService(access, logs);
  private AuthUser admin = new AuthUser(7L);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminOperationLogController(
                    service, Mappers.getMapper(AdminOperationLogMapper.class)))
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
                    return admin;
                  }
                })
            .build();
  }

  @Test
  void searchesWithAllFiltersAndReturnsCursorPage() throws Exception {
    OperationLogSearchQuery query =
        new OperationLogSearchQuery(
            "운영", FROM, TO, "DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, 30L, 10);
    when(logs.search(query))
        .thenReturn(
            new OperationLogPage(
                List.of(
                    new OperationLog(
                        29L, "DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, "운영 검토", FROM)),
                29L,
                true));

    mvc.perform(
            get(PATH)
                .param("q", "  운영  ")
                .param("from", FROM.toString())
                .param("to", TO.toString())
                .param("targetType", "DISPLAY_REVIEW_REPLY")
                .param("targetId", "41")
                .param("action", "DELETE")
                .param("operatorId", "7")
                .param("cursor", "30")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].operationId").value(29))
        .andExpect(jsonPath("$.success.data.items[0].targetType").value("DISPLAY_REVIEW_REPLY"))
        .andExpect(jsonPath("$.success.data.items[0].targetId").value(41))
        .andExpect(jsonPath("$.success.data.items[0].action").value("DELETE"))
        .andExpect(jsonPath("$.success.data.items[0].operatorId").value(7))
        .andExpect(jsonPath("$.success.data.items[0].reason").value("운영 검토"))
        .andExpect(jsonPath("$.success.data.items[0].processedAt").value(FROM.toString()))
        .andExpect(jsonPath("$.success.data.nextCursor").value(29))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));

    verify(logs).search(query);
  }

  @Test
  void usesDefaultsAndReturnsEmptyPage() throws Exception {
    OperationLogSearchQuery query =
        new OperationLogSearchQuery(null, null, null, null, null, null, null, null, 20);
    when(logs.search(query)).thenReturn(new OperationLogPage(List.of(), null, false));

    mvc.perform(get(PATH).param("q", "   "))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isEmpty())
        .andExpect(jsonPath("$.success.data.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));

    verify(logs).search(query);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      value = {
        "targetType|display_review",
        "targetType|DISPLAY-REVIEW",
        "targetId|41",
        "action|delete",
        "operatorId|0",
        "cursor|0",
        "size|0",
        "size|101"
      })
  void rejectsInvalidFilters(String name, String value) throws Exception {
    mvc.perform(get(PATH).param(name, value))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(logs);
  }

  @Test
  void rejectsTooLongSearchAndInvalidPeriod() throws Exception {
    assertThat(
            new OperationLogSearchQuery(
                    " " + "가".repeat(100) + " ", null, null, null, null, null, null, null, 20)
                .q())
        .hasSize(100);
    mvc.perform(get(PATH).param("q", "가".repeat(101)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    mvc.perform(get(PATH).param("from", TO.toString()).param("to", FROM.toString()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    mvc.perform(get(PATH).param("from", FROM.toString()).param("to", FROM.toString()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(logs);
  }

  @ParameterizedTest
  @ValueSource(strings = {"2026-11-01", "yesterday"})
  void rejectsNonIsoDateTime(String value) throws Exception {
    mvc.perform(get(PATH).param("from", value))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(logs);
  }

  @Test
  void rejectsMissingPrincipalAndOrdinaryUser() throws Exception {
    admin = null;
    mvc.perform(get(PATH))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    OperationLogSearchQuery query =
        new OperationLogSearchQuery(null, null, null, null, null, null, null, null, 20);
    assertThatThrownBy(() -> service.search(8L, query))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(logs);
  }

  @Test
  void requiresActivationToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(
                AdminOperationLogConfiguration.class, AdminOperationLogController.class)
            .withBean(
                AdminOperationLogMapper.class,
                () -> Mappers.getMapper(AdminOperationLogMapper.class))
            .withBean(AdminAccessPort.class, () -> access)
            .withBean(OperationLogPort.class, () -> logs);
    context.run(
        ctx ->
            assertThat(ctx)
                .doesNotHaveBean(AdminOperationLogService.class)
                .doesNotHaveBean(AdminOperationLogController.class));
    context
        .withPropertyValues("admin.operations.enabled=true")
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminOperationLogService.class)
                    .hasSingleBean(AdminOperationLogController.class));
  }
}
