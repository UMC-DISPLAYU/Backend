package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentStatusPort;
import com.example.demo.domain.admin.application.result.ContentStatusResult;
import com.example.demo.domain.admin.application.service.AdminContentStatusService;
import com.example.demo.domain.admin.infrastructure.config.AdminContentStatusConfiguration;
import com.example.demo.domain.admin.presentation.AdminContentStatusController;
import com.example.demo.domain.admin.presentation.mapper.AdminContentStatusMapper;
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

class AdminContentStatusTest {
  private static final String PATH =
      "/api/v1/admin/contents/{contentType}/{contentId}/operation-status";
  private static final Instant PROCESSED_AT = Instant.parse("2026-11-05T13:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ContentStatusPort contents = mock(ContentStatusPort.class);
  private final AdminContentStatusService service = new AdminContentStatusService(access, contents);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminContentStatusController(
                    service, Mappers.getMapper(AdminContentStatusMapper.class)))
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
  @CsvSource({"DISPLAY,ACTIVE", "DISPLAY,SUSPENDED", "ARTWORK,ACTIVE", "ARTWORK,SUSPENDED"})
  void changesBothTypesUsingPathIdentityAndAuthenticatedOperator(String type, String status)
      throws Exception {
    boolean visible = "ACTIVE".equals(status);
    String parentStatus = "ARTWORK".equals(type) ? "ACTIVE" : null;
    when(contents.changeStatus(type, 10L, 7L, status, "운영 검토"))
        .thenReturn(
            new ContentStatusResult(
                type,
                10L,
                status,
                parentStatus,
                visible,
                visible ? List.of() : List.of("SUSPENDED"),
                PROCESSED_AT));
    mvc.perform(
            patch(PATH, type, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"operationStatus":"%s","reason":"  운영 검토  ","operatorId":999,"contentId":99,"contentType":"PERSONAL_ARTWORK"}
            """
                        .formatted(status)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.contentType").value(type))
        .andExpect(jsonPath("$.success.data.contentId").value(10))
        .andExpect(jsonPath("$.success.data.operationStatus").value(status))
        .andExpect(
            jsonPath("$.success.data.parentOperationStatus")
                .value(parentStatus == null ? nullValue() : equalTo(parentStatus)))
        .andExpect(jsonPath("$.success.data.viewerVisible").value(visible))
        .andExpect(jsonPath("$.success.data.visibilityReasons.length()").value(visible ? 0 : 1))
        .andExpect(jsonPath("$.success.data.processedAt").value(PROCESSED_AT.toString()))
        .andExpect(jsonPath("$.error").value(nullValue()))
        .andExpect(
            jsonPath("$.meta.path")
                .value("/api/v1/admin/contents/" + type + "/10/operation-status"));
    verify(contents).changeStatus(type, 10L, 7L, status, "운영 검토");
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @CsvSource({
    "ARTWORK,SUSPENDED,PARENT_SUSPENDED",
    "ARTWORK,ACTIVE,SCHEDULED",
    "DISPLAY,,SCHEDULED"
  })
  void preservesDomainVisibilityWhenResumingWithoutAssumingPublicAccess(
      String type, String parent, String reason) throws Exception {
    when(contents.changeStatus(type, 10L, 7L, "ACTIVE", "사유"))
        .thenReturn(
            new ContentStatusResult(
                type, 10L, "ACTIVE", parent, false, List.of(reason), PROCESSED_AT));
    mvc.perform(
            patch(PATH, type, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("ACTIVE", "사유")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.operationStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.viewerVisible").value(false))
        .andExpect(jsonPath("$.success.data.visibilityReasons[0]").value(reason));
    verify(contents).changeStatus(type, 10L, 7L, "ACTIVE", "사유");
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"PERSONAL_ARTWORK", "LOUNGE", "display", "UNKNOWN"})
  void rejectsUnsupportedContentTypes(String type) throws Exception {
    mvc.perform(
            patch(PATH, type, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("ACTIVE", "사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"reason\":\"사유\"}",
        "{\"operationStatus\":null,\"reason\":\"사유\"}",
        "{\"operationStatus\":\"active\",\"reason\":\"사유\"}",
        "{\"operationStatus\":\"PUBLISHED\",\"reason\":\"사유\"}",
        "{\"operationStatus\":\" ACTIVE \",\"reason\":\"사유\"}",
        "{\"operationStatus\":\"SUSPENDED\"}",
        "{\"operationStatus\":\"ACTIVE\",\"reason\":null}",
        "{\"operationStatus\":\"ACTIVE\",\"reason\":\"\"}",
        "{\"operationStatus\":\"SUSPENDED\",\"reason\":\"   \"}"
      })
  void rejectsInvalidBody(String body) throws Exception {
    mvc.perform(patch(PATH, "DISPLAY", 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"ACTIVE", "SUSPENDED"})
  void validatesReasonLengthAfterStrippingWhitespace(String status) throws Exception {
    for (int length : new int[] {1, 1000}) {
      String reason = "가".repeat(length);
      when(contents.changeStatus("DISPLAY", 10L, 7L, status, reason))
          .thenReturn(
              new ContentStatusResult(
                  "DISPLAY", 10L, status, null, false, List.of("SCHEDULED"), PROCESSED_AT));
      mvc.perform(
              patch(PATH, "DISPLAY", 10)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body(status, "  " + reason + "  ")))
          .andExpect(status().isOk());
      verify(contents).changeStatus("DISPLAY", 10L, 7L, status, reason);
    }
    mvc.perform(
            patch(PATH, "DISPLAY", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(status, "가".repeat(1001))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @CsvSource({"0,INVALID_INPUT_VALUE", "-1,INVALID_INPUT_VALUE", "abc,INVALID_TYPE_VALUE"})
  void rejectsInvalidContentId(String id, String code) throws Exception {
    mvc.perform(
            patch(PATH, "DISPLAY", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("ACTIVE", "사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value(code));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "null", "{", "{\"operationStatus\":[]}"})
  void rejectsMissingOrMalformedJson(String body) throws Exception {
    mvc.perform(patch(PATH, "ARTWORK", 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("MALFORMED_JSON"));
    verifyNoInteractions(contents);
  }

  @Test
  void serviceRejectsAnonymousAndOrdinaryOperatorsBeforeProcessingInput() {
    assertThatThrownBy(() -> service.changeStatus(null, null, null, null, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.UNAUTHORIZED));
    assertThatThrownBy(() -> service.changeStatus(9L, null, null, null, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(contents);
  }

  @Test
  void serviceRejectsNullTypeAndId() {
    assertThatThrownBy(() -> service.changeStatus(7L, null, 10L, "ACTIVE", "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.changeStatus(7L, "DISPLAY", null, "ACTIVE", "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @CsvSource({
    "DISPLAY,NOT_FOUND,404",
    "ARTWORK,NOT_FOUND,404",
    "DISPLAY,CONFLICT,409",
    "ARTWORK,CONFLICT,409"
  })
  void preservesDomainErrorsWithoutRetrying(String type, String code, int httpStatus)
      throws Exception {
    when(contents.changeStatus(type, 10L, 7L, "SUSPENDED", "사유"))
        .thenThrow(new BusinessException(GlobalErrorCode.valueOf(code)));
    mvc.perform(
            patch(PATH, type, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("SUSPENDED", "사유")))
        .andExpect(status().is(httpStatus))
        .andExpect(jsonPath("$.error.code").value(code));
    verify(contents).changeStatus(type, 10L, 7L, "SUSPENDED", "사유");
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = "false")
  void featureIsDisabledUnlessExplicitlyEnabled(String enabled) {
    ApplicationContextRunner runner =
        contextRunner().withPropertyValues("admin.contents.enabled=true");
    if (enabled != null)
      runner = runner.withPropertyValues("admin.content-status.enabled=" + enabled);
    runner.run(
        context -> {
          assertThat(context).hasNotFailed();
          assertThat(context).doesNotHaveBean(AdminContentStatusController.class);
          assertThat(context).doesNotHaveBean(AdminContentStatusService.class);
        });
  }

  @Test
  void enabledFeatureFailsWithoutContentPort() {
    contextRunner()
        .withPropertyValues("admin.content-status.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void enabledFeatureRegistersControllerAndServiceWithPorts() {
    contextRunner()
        .withPropertyValues("admin.content-status.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ContentStatusPort.class, () -> contents)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(AdminContentStatusController.class);
              assertThat(context).hasSingleBean(AdminContentStatusService.class);
            });
  }

  private ApplicationContextRunner contextRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(
            AdminContentStatusConfiguration.class, AdminContentStatusController.class)
        .withBean(
            AdminContentStatusMapper.class,
            () -> Mappers.getMapper(AdminContentStatusMapper.class));
  }

  private String body(String status, String reason) {
    return "{\"operationStatus\":\"" + status + "\",\"reason\":\"" + reason + "\"}";
  }
}
