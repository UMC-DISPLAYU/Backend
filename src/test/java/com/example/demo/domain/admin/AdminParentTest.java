package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ParentQueryPort;
import com.example.demo.domain.admin.application.result.ParentDetail;
import com.example.demo.domain.admin.application.service.AdminParentService;
import com.example.demo.domain.admin.infrastructure.config.AdminParentConfiguration;
import com.example.demo.domain.admin.presentation.AdminParentController;
import com.example.demo.domain.admin.presentation.mapper.AdminParentMapper;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.AuthUser;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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

class AdminParentTest {
  private static final Instant CREATED_AT = Instant.parse("2026-11-02T12:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ParentQueryPort parents = mock(ParentQueryPort.class);
  private final AdminParentService service = new AdminParentService(access, parents);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminParentController(service, Mappers.getMapper(AdminParentMapper.class)))
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

  @Test
  void returnsParentAndContextWithoutChangingDomainCodes() throws Exception {
    var attachment = new ParentDetail.Attachment("https://example.com/image.jpg", "IMAGE");
    var parent =
        new ParentDetail.Parent(
            "DISPLAY_REVIEW", 31L, 12L, "후기 작성자", "후기 원문", List.of(attachment), CREATED_AT, null);
    when(parents.getDetail("DISPLAY_REVIEW", 31L)).thenReturn(new ParentDetail(parent, 21L, null));

    mvc.perform(get("/api/v1/admin/parents/DISPLAY_REVIEW/31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.parent.parentType").value("DISPLAY_REVIEW"))
        .andExpect(jsonPath("$.success.data.parent.parentId").value(31))
        .andExpect(jsonPath("$.success.data.parent.authorUserId").value(12))
        .andExpect(jsonPath("$.success.data.parent.authorNickname").value("후기 작성자"))
        .andExpect(jsonPath("$.success.data.parent.content").value("후기 원문"))
        .andExpect(
            jsonPath("$.success.data.parent.attachments[0].url")
                .value("https://example.com/image.jpg"))
        .andExpect(jsonPath("$.success.data.parent.attachments[0].type").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.parent.createdAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.parent.deletedAt").value(nullValue()))
        .andExpect(jsonPath("$.success.data.displayId").value(21))
        .andExpect(jsonPath("$.success.data.artworkId").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/parents/DISPLAY_REVIEW/31"));
  }

  @Test
  void returnsRetainedDeletedParentWithWithdrawnAuthor() throws Exception {
    var parent =
        new ParentDetail.Parent(
            "ARTWORK_FEELING", 31L, null, null, null, List.of(), CREATED_AT, CREATED_AT);
    when(parents.getDetail("ARTWORK_FEELING", 31L)).thenReturn(new ParentDetail(parent, 21L, 22L));

    mvc.perform(get("/api/v1/admin/parents/ARTWORK_FEELING/31"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.parent.authorUserId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parent.authorNickname").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parent.content").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parent.attachments").isEmpty())
        .andExpect(jsonPath("$.success.data.parent.deletedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.displayId").value(21))
        .andExpect(jsonPath("$.success.data.artworkId").value(22));
  }

  @ParameterizedTest
  @ValueSource(strings = {"parent", "1PARENT", "PARENT-TYPE", "PARENT.TYPE"})
  void rejectsInvalidParentTypeBeforeCallingPort(String parentType) throws Exception {
    mvc.perform(get("/api/v1/admin/parents/" + parentType + "/31"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(parents);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1"})
  void rejectsNonPositiveParentIdBeforeCallingPort(String parentId) throws Exception {
    mvc.perform(get("/api/v1/admin/parents/PARENT_KIND/" + parentId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(parents);
  }

  @Test
  void rejectsNonNumericParentId() throws Exception {
    mvc.perform(get("/api/v1/admin/parents/PARENT_KIND/invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(parents);
  }

  @Test
  void propagatesMissingParentAsCommonNotFoundError() throws Exception {
    when(parents.getDetail("PARENT_KIND", 31L))
        .thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));

    mvc.perform(get("/api/v1/admin/parents/PARENT_KIND/31"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {8L})
  void rejectsUnauthorizedCallersBeforeQueryingParents(Long adminId) {
    GlobalErrorCode error =
        adminId == null ? GlobalErrorCode.UNAUTHORIZED : GlobalErrorCode.FORBIDDEN;
    assertThatThrownBy(() -> service.getDetail(adminId, "PARENT_KIND", 31L))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    verifyNoInteractions(parents);
  }

  @Test
  void rejectsNullInputsBeforeCallingPort() {
    assertThatThrownBy(() -> service.getDetail(7L, null, 31L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.getDetail(7L, "PARENT_KIND", null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(parents);
  }

  @Test
  void requiresActivationAndBothPortsToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(AdminParentConfiguration.class, AdminParentController.class)
            .withBean(AdminParentMapper.class, () -> Mappers.getMapper(AdminParentMapper.class));
    context.run(
        ctx ->
            assertThat(ctx)
                .doesNotHaveBean(AdminParentService.class)
                .doesNotHaveBean(AdminParentController.class));
    context
        .withPropertyValues("admin.parents.enabled=false")
        .run(
            ctx ->
                assertThat(ctx)
                    .doesNotHaveBean(AdminParentService.class)
                    .doesNotHaveBean(AdminParentController.class));
    context
        .withPropertyValues("admin.parents.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.parents.enabled=true")
        .withBean(ParentQueryPort.class, () -> parents)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.parents.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ParentQueryPort.class, () -> parents)
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminParentService.class)
                    .hasSingleBean(AdminParentController.class));
  }
}
