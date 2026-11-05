package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentDeletionPort;
import com.example.demo.domain.admin.application.result.ContentDeletionResult;
import com.example.demo.domain.admin.application.service.AdminContentDeletionService;
import com.example.demo.domain.admin.infrastructure.config.AdminContentDeletionConfiguration;
import com.example.demo.domain.admin.presentation.AdminContentDeletionController;
import com.example.demo.domain.admin.presentation.mapper.AdminContentDeletionMapper;
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

class AdminContentDeletionTest {
  private static final String PATH = "/api/v1/admin/contents/{contentType}/{contentId}/delete";
  private static final Instant PROCESSED_AT = Instant.parse("2026-11-05T13:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ContentDeletionPort contents = mock(ContentDeletionPort.class);
  private final AdminContentDeletionService service =
      new AdminContentDeletionService(access, contents);
  private AuthUser operator = new AuthUser(7L);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminContentDeletionController(
                    service, Mappers.getMapper(AdminContentDeletionMapper.class)))
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
  @ValueSource(strings = {"DISPLAY", "ARTWORK"})
  void deletesUsingPathIdentityAndAuthenticatedOperator(String type) throws Exception {
    when(contents.delete(type, 10L, 7L, "운영 검토"))
        .thenReturn(new ContentDeletionResult(type, 10L, PROCESSED_AT));
    mvc.perform(
            post(PATH, type, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"reason":"  운영 검토  ","operatorId":999,"contentId":99,"contentType":"PERSONAL_ARTWORK"}
            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.deletedTargetType").value(type))
        .andExpect(jsonPath("$.success.data.deletedTargetId").value(10))
        .andExpect(jsonPath("$.success.data.processedAt").value(PROCESSED_AT.toString()))
        .andExpect(jsonPath("$.error").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/contents/" + type + "/10/delete"));
    verify(contents).delete(type, 10L, 7L, "운영 검토");
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"PERSONAL_ARTWORK", "SECTION", "PHOTO", "LOUNGE", "display"})
  void rejectsUnsupportedContentTypes(String type) throws Exception {
    mvc.perform(post(PATH, type, 10).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"reason\":null}",
        "{\"reason\":\"\"}",
        "{\"reason\":\"   \"}",
        "{\"reason\":\"\\t\\n\"}"
      })
  void rejectsMissingOrBlankReason(String body) throws Exception {
    mvc.perform(post(PATH, "DISPLAY", 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @Test
  void validatesReasonLengthAfterStrippingWhitespace() throws Exception {
    for (int length : new int[] {1, 1000}) {
      String reason = "가".repeat(length);
      when(contents.delete("DISPLAY", 10L, 7L, reason))
          .thenReturn(new ContentDeletionResult("DISPLAY", 10L, PROCESSED_AT));
      mvc.perform(
              post(PATH, "DISPLAY", 10)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(body("  " + reason + "  ")))
          .andExpect(status().isOk());
      verify(contents).delete("DISPLAY", 10L, 7L, reason);
    }
    mvc.perform(
            post(PATH, "DISPLAY", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("가".repeat(1001))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @CsvSource({"0,INVALID_INPUT_VALUE", "-1,INVALID_INPUT_VALUE", "abc,INVALID_TYPE_VALUE"})
  void rejectsInvalidContentId(String id, String code) throws Exception {
    mvc.perform(
            post(PATH, "DISPLAY", id).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value(code));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "null", "{", "{\"reason\":[]}"})
  void rejectsMissingOrMalformedJson(String body) throws Exception {
    mvc.perform(post(PATH, "ARTWORK", 10).contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("MALFORMED_JSON"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"GET", "DELETE", "PATCH"})
  void onlyPostCanInvokeDeletion(String method) throws Exception {
    mvc.perform(
            request(HttpMethod.valueOf(method), PATH, "DISPLAY", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유")))
        .andExpect(status().isMethodNotAllowed());
    verifyNoInteractions(contents);
  }

  @Test
  void controllerRejectsMissingPrincipal() throws Exception {
    operator = null;
    mvc.perform(
            post(PATH, "DISPLAY", 10).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    verifyNoInteractions(contents);
  }

  @Test
  void serviceRejectsOrdinaryOperatorBeforeProcessingInput() {
    assertThatThrownBy(() -> service.delete(9L, null, null, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(contents);
  }

  @Test
  void serviceRejectsNullTypeAndId() {
    assertThatThrownBy(() -> service.delete(7L, null, 10L, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.delete(7L, "DISPLAY", null, "사유"))
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
    "ARTWORK,CONFLICT,409",
    "DISPLAY,INTERNAL_SERVER_ERROR,500"
  })
  void preservesDomainErrorsWithoutRetrying(String type, String code, int httpStatus)
      throws Exception {
    when(contents.delete(type, 10L, 7L, "사유"))
        .thenThrow(new BusinessException(GlobalErrorCode.valueOf(code)));
    mvc.perform(post(PATH, type, 10).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().is(httpStatus))
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value(code));
    verify(contents).delete(type, 10L, 7L, "사유");
    verifyNoMoreInteractions(contents);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = "false")
  void featureIsDisabledUnlessExplicitlyEnabled(String enabled) {
    ApplicationContextRunner runner =
        contextRunner()
            .withPropertyValues("admin.contents.enabled=true", "admin.content-status.enabled=true");
    if (enabled != null)
      runner = runner.withPropertyValues("admin.content-deletion.enabled=" + enabled);
    runner.run(
        context -> {
          assertThat(context).hasNotFailed();
          assertThat(context).doesNotHaveBean(AdminContentDeletionController.class);
          assertThat(context).doesNotHaveBean(AdminContentDeletionService.class);
        });
  }

  @Test
  void enabledFeatureFailsWithoutDeletionPort() {
    contextRunner()
        .withPropertyValues("admin.content-deletion.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(context -> assertThat(context).hasFailed());
  }

  @Test
  void enabledFeatureRegistersControllerAndServiceWithPorts() {
    contextRunner()
        .withPropertyValues("admin.content-deletion.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ContentDeletionPort.class, () -> contents)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(AdminContentDeletionController.class);
              assertThat(context).hasSingleBean(AdminContentDeletionService.class);
            });
  }

  private ApplicationContextRunner contextRunner() {
    return new ApplicationContextRunner()
        .withUserConfiguration(
            AdminContentDeletionConfiguration.class, AdminContentDeletionController.class)
        .withBean(
            AdminContentDeletionMapper.class,
            () -> Mappers.getMapper(AdminContentDeletionMapper.class));
  }

  private String body(String reason) {
    return "{\"reason\":\"" + reason + "\"}";
  }
}
