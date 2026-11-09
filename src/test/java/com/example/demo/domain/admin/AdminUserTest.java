package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.UserQueryPort;
import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.result.UserDetail;
import com.example.demo.domain.admin.application.result.UserPage;
import com.example.demo.domain.admin.application.result.UserSummary;
import com.example.demo.domain.admin.application.service.AdminUserService;
import com.example.demo.domain.admin.infrastructure.config.AdminUserConfiguration;
import com.example.demo.domain.admin.presentation.AdminUserController;
import com.example.demo.domain.admin.presentation.mapper.AdminUserMapper;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

class AdminUserTest {
  private static final Instant CREATED_AT = Instant.parse("2026-11-02T12:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final UserQueryPort users = mock(UserQueryPort.class);
  private final AdminUserService service = new AdminUserService(access, users);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminUserController(service, Mappers.getMapper(AdminUserMapper.class)))
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
  void returnsDefaultPageInCommonEnvelope() throws Exception {
    when(users.search(new UserSearchQuery(null, null, null, 20)))
        .thenReturn(
            new UserPage(
                List.of(new UserSummary(10L, "작가", "artist@example.com", "ACTIVE", CREATED_AT)),
                10L,
                true));

    mvc.perform(get("/api/v1/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.items[0].userId").value(10))
        .andExpect(jsonPath("$.success.data.items[0].nickname").value("작가"))
        .andExpect(jsonPath("$.success.data.items[0].email").value("artist@example.com"))
        .andExpect(jsonPath("$.success.data.items[0].accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.items[0].createdAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.nextCursor").value(10))
        .andExpect(jsonPath("$.success.data.hasNext").value(true))
        .andExpect(jsonPath("$.error").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/users"));
    verify(users).search(new UserSearchQuery(null, null, null, 20));
  }

  @Test
  void passesNormalizedSearchAndFiltersAndReturnsEmptyLastPage() throws Exception {
    when(users.search(any())).thenReturn(new UserPage(List.of(), null, false));

    mvc.perform(
            get("/api/v1/admin/users")
                .param("q", "  artist@example.com  ")
                .param("accountStatus", "SUSPENDED")
                .param("cursor", "10")
                .param("size", "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isEmpty())
        .andExpect(jsonPath("$.success.data.nextCursor").value(nullValue()))
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
    verify(users).search(new UserSearchQuery("artist@example.com", "SUSPENDED", 10L, 100));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t\n", "\u2003"})
  void blankSearchDoesNotAddSearchCondition(String q) {
    assertThat(new UserSearchQuery(q, null, null, 20).q()).isNull();
  }

  @Test
  void acceptsBoundaryValuesAfterTrimmingSearch() throws Exception {
    String q = "가".repeat(100);
    when(users.search(any())).thenReturn(new UserPage(List.of(), null, false));

    mvc.perform(
            get("/api/v1/admin/users")
                .param("q", " " + q + " ")
                .param("accountStatus", "ACTIVE")
                .param("cursor", "1")
                .param("size", "1"))
        .andExpect(status().isOk());
    verify(users).search(new UserSearchQuery(q, "ACTIVE", 1L, 1));
  }

  @ParameterizedTest
  @CsvSource({
    "cursor,0",
    "cursor,-1",
    "size,0",
    "size,101",
    "accountStatus,DELETED",
    "accountStatus,active",
    "accountStatus,''"
  })
  void rejectsInvalidFiltersWithoutCallingPort(String parameter, String value) throws Exception {
    mvc.perform(get("/api/v1/admin/users").param(parameter, value))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(users);
  }

  @Test
  void rejectsSearchLongerThanOneHundredCharacters() throws Exception {
    mvc.perform(get("/api/v1/admin/users").param("q", "가".repeat(101)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @ValueSource(strings = {"cursor", "size"})
  void rejectsNonNumericQueryValues(String parameter) throws Exception {
    mvc.perform(get("/api/v1/admin/users").param(parameter, "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(users);
  }

  @Test
  void returnsActiveUserWithNullSuspensionFieldsAndEmptyHistory() throws Exception {
    when(users.getDetail(10L))
        .thenReturn(
            new UserDetail(10L, "작가", null, "ACTIVE", CREATED_AT, null, null, null, List.of()));

    mvc.perform(get("/api/v1/admin/users/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.userId").value(10))
        .andExpect(jsonPath("$.success.data.nickname").value("작가"))
        .andExpect(jsonPath("$.success.data.email").value(nullValue()))
        .andExpect(jsonPath("$.success.data.accountStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.createdAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.suspendedAt").value(nullValue()))
        .andExpect(jsonPath("$.success.data.operatorId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.reason").value(nullValue()))
        .andExpect(jsonPath("$.success.data.history").isEmpty());
    verify(access).isAdmin(7L);
    verify(users).getDetail(10L);
  }

  @Test
  void mapsSuspensionAndHistoryWithoutChangingOperatorOrOrder() throws Exception {
    Instant processedAt = CREATED_AT.plusSeconds(60);
    when(users.getDetail(10L))
        .thenReturn(
            new UserDetail(
                10L,
                "작가",
                "artist@example.com",
                "SUSPENDED",
                CREATED_AT,
                processedAt,
                8L,
                "운영 조치",
                List.of(
                    new UserDetail.History(2L, "SUSPEND", 8L, "운영 조치", processedAt),
                    new UserDetail.History(1L, "UNSUSPEND", 9L, null, CREATED_AT))));

    mvc.perform(get("/api/v1/admin/users/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.accountStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.success.data.suspendedAt").value(processedAt.toString()))
        .andExpect(jsonPath("$.success.data.operatorId").value(8))
        .andExpect(jsonPath("$.success.data.reason").value("운영 조치"))
        .andExpect(jsonPath("$.success.data.history[0].operationId").value(2))
        .andExpect(jsonPath("$.success.data.history[0].action").value("SUSPEND"))
        .andExpect(jsonPath("$.success.data.history[0].operatorId").value(8))
        .andExpect(jsonPath("$.success.data.history[0].reason").value("운영 조치"))
        .andExpect(jsonPath("$.success.data.history[0].processedAt").value(processedAt.toString()))
        .andExpect(jsonPath("$.success.data.history[1].operationId").value(1))
        .andExpect(jsonPath("$.success.data.history[1].reason").value(nullValue()));
  }

  @Test
  void propagatesMissingUserAsCommonNotFoundError() throws Exception {
    when(users.getDetail(10L)).thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));

    mvc.perform(get("/api/v1/admin/users/10"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.success").value(nullValue()))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/users/10"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1"})
  void rejectsNonPositiveTargetIds(String id) throws Exception {
    mvc.perform(get("/api/v1/admin/users/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(users);
  }

  @Test
  void rejectsNonNumericTargetId() throws Exception {
    mvc.perform(get("/api/v1/admin/users/invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(users);
  }

  @Test
  void rejectsNullTargetIdBeforeCallingPort() {
    assertThatThrownBy(() -> service.getDetail(7L, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(users);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {8L})
  void rejectsUnauthorizedCallersBeforeQueryingUsers(Long adminId) {
    GlobalErrorCode error =
        adminId == null ? GlobalErrorCode.UNAUTHORIZED : GlobalErrorCode.FORBIDDEN;
    assertThatThrownBy(() -> service.search(adminId, new UserSearchQuery(null, null, null, 20)))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    assertThatThrownBy(() -> service.getDetail(adminId, 10L))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    verifyNoInteractions(users);
  }

  @Test
  void requiresActivationAndBothPortsToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(AdminUserConfiguration.class, AdminUserController.class)
            .withBean(AdminUserMapper.class, () -> Mappers.getMapper(AdminUserMapper.class));
    context.run(
        ctx -> {
          assertThat(ctx).hasNotFailed();
          assertThat(ctx)
              .doesNotHaveBean(AdminUserService.class)
              .doesNotHaveBean(AdminUserController.class);
        });
    context
        .withPropertyValues("admin.users.enabled=false")
        .run(
            ctx -> {
              assertThat(ctx).hasNotFailed();
              assertThat(ctx)
                  .doesNotHaveBean(AdminUserService.class)
                  .doesNotHaveBean(AdminUserController.class);
            });
    context
        .withPropertyValues("admin.users.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.users.enabled=true")
        .withBean(UserQueryPort.class, () -> users)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.users.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(UserQueryPort.class, () -> users)
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminUserService.class)
                    .hasSingleBean(AdminUserController.class));
  }
}
