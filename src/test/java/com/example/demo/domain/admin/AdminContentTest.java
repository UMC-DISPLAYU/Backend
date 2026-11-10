package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ContentQueryPort;
import com.example.demo.domain.admin.application.query.ContentSearchQuery;
import com.example.demo.domain.admin.application.result.ContentDetail;
import com.example.demo.domain.admin.application.result.ContentPage;
import com.example.demo.domain.admin.application.result.ContentSummary;
import com.example.demo.domain.admin.application.service.AdminContentService;
import com.example.demo.domain.admin.infrastructure.config.AdminContentConfiguration;
import com.example.demo.domain.admin.presentation.AdminContentController;
import com.example.demo.domain.admin.presentation.mapper.AdminContentMapper;
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

class AdminContentTest {
  private static final Instant CREATED_AT = Instant.parse("2026-11-02T12:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ContentQueryPort contents = mock(ContentQueryPort.class);
  private final AdminContentService service = new AdminContentService(access, contents);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminContentController(service, Mappers.getMapper(AdminContentMapper.class)))
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
  void returnsMixedTypesWithSameIdAndStringCursorWithoutInferringVisibility() throws Exception {
    String nextCursor = "v1.ARTWORK.MTA=";
    when(contents.search(new ContentSearchQuery(null, null, null, null, 20)))
        .thenReturn(
            new ContentPage(
                List.of(
                    new ContentSummary(
                        "DISPLAY",
                        10L,
                        "전시",
                        11L,
                        "대표작가",
                        "ACTIVE",
                        null,
                        true,
                        List.of(),
                        CREATED_AT,
                        "https://example.com/poster.jpg"),
                    new ContentSummary(
                        "ARTWORK",
                        10L,
                        "작품",
                        null,
                        "직접 입력 작가",
                        "ACTIVE",
                        "SUSPENDED",
                        false,
                        List.of("PARENT_SUSPENDED"),
                        CREATED_AT,
                        null)),
                nextCursor,
                true));

    mvc.perform(get("/api/v1/admin/contents"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.items.length()").value(2))
        .andExpect(jsonPath("$.success.data.items[0].contentType").value("DISPLAY"))
        .andExpect(jsonPath("$.success.data.items[0].contentId").value(10))
        .andExpect(jsonPath("$.success.data.items[0].title").value("전시"))
        .andExpect(jsonPath("$.success.data.items[0].ownerUserId").value(11))
        .andExpect(jsonPath("$.success.data.items[0].ownerName").value("대표작가"))
        .andExpect(jsonPath("$.success.data.items[0].parentOperationStatus").value(nullValue()))
        .andExpect(jsonPath("$.success.data.items[0].viewerVisible").value(true))
        .andExpect(jsonPath("$.success.data.items[0].visibilityReasons").isEmpty())
        .andExpect(jsonPath("$.success.data.items[0].createdAt").value(CREATED_AT.toString()))
        .andExpect(
            jsonPath("$.success.data.items[0].thumbnailUrl")
                .value("https://example.com/poster.jpg"))
        .andExpect(jsonPath("$.success.data.items[1].contentType").value("ARTWORK"))
        .andExpect(jsonPath("$.success.data.items[1].contentId").value(10))
        .andExpect(jsonPath("$.success.data.items[1].ownerUserId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.items[1].ownerName").value("직접 입력 작가"))
        .andExpect(jsonPath("$.success.data.items[1].operationStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.items[1].parentOperationStatus").value("SUSPENDED"))
        .andExpect(jsonPath("$.success.data.items[1].viewerVisible").value(false))
        .andExpect(
            jsonPath("$.success.data.items[1].visibilityReasons[0]").value("PARENT_SUSPENDED"))
        .andExpect(jsonPath("$.success.data.items[1].thumbnailUrl").value(nullValue()))
        .andExpect(jsonPath("$.success.data.nextCursor").value(nextCursor))
        .andExpect(jsonPath("$.success.data.hasNext").value(true))
        .andExpect(jsonPath("$.error").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/contents"));
    verify(contents).search(new ContentSearchQuery(null, null, null, null, 20));
  }

  @Test
  void passesFiltersAndOpaqueCursorUnchangedAndReturnsEmptyLastPage() throws Exception {
    String cursor = "cursor-v1_ARTWORK-MTA=";
    when(contents.search(any())).thenReturn(new ContentPage(List.of(), null, false));

    mvc.perform(
            get("/api/v1/admin/contents")
                .param("q", "  졸업 전시  ")
                .param("contentType", "ARTWORK")
                .param("operationStatus", "SUSPENDED")
                .param("cursor", cursor)
                .param("size", "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items").isEmpty())
        .andExpect(jsonPath("$.success.data.nextCursor").value(nullValue()))
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
    verify(contents).search(new ContentSearchQuery("졸업 전시", "ARTWORK", "SUSPENDED", cursor, 100));
  }

  @Test
  void acceptsSearchCursorAndSizeBoundaries() throws Exception {
    String q = "가".repeat(100);
    String cursor = "a".repeat(1024);
    when(contents.search(any())).thenReturn(new ContentPage(List.of(), null, false));
    mvc.perform(
            get("/api/v1/admin/contents")
                .param("q", " " + q + " ")
                .param("contentType", "DISPLAY")
                .param("operationStatus", "ACTIVE")
                .param("cursor", cursor)
                .param("size", "1"))
        .andExpect(status().isOk());
    verify(contents).search(new ContentSearchQuery(q, "DISPLAY", "ACTIVE", cursor, 1));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"  ", "\t\n", "\u2003"})
  void blankSearchDoesNotAddSearchCondition(String q) {
    assertThat(new ContentSearchQuery(q, null, null, null, 20).q()).isNull();
  }

  @ParameterizedTest
  @CsvSource({
    "size,0",
    "size,101",
    "contentType,PERSONAL_ARTWORK",
    "contentType,LOUNGE",
    "contentType,display",
    "contentType,''",
    "operationStatus,PUBLISHED",
    "operationStatus,active",
    "operationStatus,''",
    "cursor,''"
  })
  void rejectsInvalidListInputsWithoutCallingPort(String parameter, String value) throws Exception {
    mvc.perform(get("/api/v1/admin/contents").param(parameter, value))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "\t\n", "\u2003"})
  void rejectsBlankCursorWithoutTurningItIntoFirstPage(String cursor) throws Exception {
    mvc.perform(get("/api/v1/admin/contents").param("cursor", cursor))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @CsvSource({"q,101", "cursor,1025"})
  void rejectsOversizedInput(String parameter, int length) throws Exception {
    mvc.perform(get("/api/v1/admin/contents").param(parameter, "가".repeat(length)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @Test
  void preservesAdapterCursorValidationError() throws Exception {
    when(contents.search(any()))
        .thenThrow(new BusinessException(GlobalErrorCode.INVALID_INPUT_VALUE));
    mvc.perform(get("/api/v1/admin/contents").param("cursor", "invalid-token"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verify(contents).search(new ContentSearchQuery(null, null, null, "invalid-token", 20));
  }

  @Test
  void returnsUnpublishedDetailAndPreservesImagesAndHistory() throws Exception {
    when(contents.getDetail("ARTWORK", 10L))
        .thenReturn(
            new ContentDetail(
                "ARTWORK",
                10L,
                "작품",
                11L,
                "작가",
                "ACTIVE",
                "ACTIVE",
                false,
                List.of("SCHEDULED"),
                CREATED_AT,
                "https://example.com/main.jpg",
                "작품 소개",
                List.of("https://example.com/main.jpg", "https://example.com/detail.jpg"),
                List.of(
                    new ContentDetail.History(501L, "RESUME", 9L, null, CREATED_AT),
                    new ContentDetail.History(
                        500L, "SUSPEND", 8L, "운영 검토", CREATED_AT.minusSeconds(60)))));

    mvc.perform(get("/api/v1/admin/contents/ARTWORK/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.contentType").value("ARTWORK"))
        .andExpect(jsonPath("$.success.data.contentId").value(10))
        .andExpect(jsonPath("$.success.data.title").value("작품"))
        .andExpect(jsonPath("$.success.data.ownerUserId").value(11))
        .andExpect(jsonPath("$.success.data.ownerName").value("작가"))
        .andExpect(jsonPath("$.success.data.operationStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.parentOperationStatus").value("ACTIVE"))
        .andExpect(jsonPath("$.success.data.viewerVisible").value(false))
        .andExpect(jsonPath("$.success.data.visibilityReasons[0]").value("SCHEDULED"))
        .andExpect(jsonPath("$.success.data.createdAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.thumbnailUrl").value("https://example.com/main.jpg"))
        .andExpect(jsonPath("$.success.data.content").value("작품 소개"))
        .andExpect(jsonPath("$.success.data.imageUrls[1]").value("https://example.com/detail.jpg"))
        .andExpect(jsonPath("$.success.data.history[0].operationId").value(501))
        .andExpect(jsonPath("$.success.data.history[0].action").value("RESUME"))
        .andExpect(jsonPath("$.success.data.history[0].operatorId").value(9))
        .andExpect(jsonPath("$.success.data.history[0].reason").value(nullValue()))
        .andExpect(jsonPath("$.success.data.history[0].processedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.history[1].operationId").value(500))
        .andExpect(jsonPath("$.success.data.history[1].reason").value("운영 검토"))
        .andExpect(jsonPath("$.success.data.invitationToken").doesNotExist());
    verify(access).isAdmin(7L);
    verify(contents).getDetail("ARTWORK", 10L);
  }

  @Test
  void returnsDisplayWithNullableSourceFieldsAndEmptyCollections() throws Exception {
    when(contents.getDetail("DISPLAY", 10L))
        .thenReturn(
            new ContentDetail(
                "DISPLAY",
                10L,
                "전시",
                null,
                null,
                "ACTIVE",
                null,
                true,
                List.of(),
                CREATED_AT,
                null,
                null,
                List.of(),
                List.of()));
    mvc.perform(get("/api/v1/admin/contents/DISPLAY/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.ownerUserId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.ownerName").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parentOperationStatus").value(nullValue()))
        .andExpect(jsonPath("$.success.data.thumbnailUrl").value(nullValue()))
        .andExpect(jsonPath("$.success.data.content").value(nullValue()))
        .andExpect(jsonPath("$.success.data.visibilityReasons").isEmpty())
        .andExpect(jsonPath("$.success.data.imageUrls").isEmpty())
        .andExpect(jsonPath("$.success.data.history").isEmpty());
  }

  @Test
  void propagatesMissingContentAsCommonNotFoundError() throws Exception {
    when(contents.getDetail("DISPLAY", 10L))
        .thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));
    mvc.perform(get("/api/v1/admin/contents/DISPLAY/10"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.success").value(nullValue()))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/contents/DISPLAY/10"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"DISPLAY/0", "ARTWORK/-1", "PERSONAL_ARTWORK/10", "LOUNGE/10", "display/10"})
  void rejectsInvalidDetailInputsWithoutCallingPort(String path) throws Exception {
    mvc.perform(get("/api/v1/admin/contents/" + path))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {"/api/v1/admin/contents?size=invalid", "/api/v1/admin/contents/DISPLAY/invalid"})
  void rejectsWrongNumericTypes(String path) throws Exception {
    mvc.perform(get(path))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(contents);
  }

  @Test
  void rejectsNullDetailArgumentsBeforeCallingPort() {
    assertThatThrownBy(() -> service.getDetail(7L, null, 10L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.getDetail(7L, "DISPLAY", null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(contents);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {8L})
  void deniesUnauthorizedCallersBeforeQueryingContent(Long adminId) {
    GlobalErrorCode expected =
        adminId == null ? GlobalErrorCode.UNAUTHORIZED : GlobalErrorCode.FORBIDDEN;
    assertThatThrownBy(
            () -> service.search(adminId, new ContentSearchQuery(null, null, null, null, 20)))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(expected));
    assertThatThrownBy(() -> service.getDetail(adminId, "DISPLAY", 10L))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(expected));
    verifyNoInteractions(contents);
  }

  @Test
  void requiresActivationAndBothPortsToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(AdminContentConfiguration.class, AdminContentController.class)
            .withBean(AdminContentMapper.class, () -> Mappers.getMapper(AdminContentMapper.class));
    context.run(
        ctx -> {
          assertThat(ctx).hasNotFailed();
          assertThat(ctx)
              .doesNotHaveBean(AdminContentService.class)
              .doesNotHaveBean(AdminContentController.class);
        });
    context
        .withPropertyValues("admin.contents.enabled=false")
        .run(
            ctx -> {
              assertThat(ctx).hasNotFailed();
              assertThat(ctx)
                  .doesNotHaveBean(AdminContentService.class)
                  .doesNotHaveBean(AdminContentController.class);
            });
    context
        .withPropertyValues("admin.contents.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.contents.enabled=true")
        .withBean(ContentQueryPort.class, () -> contents)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.contents.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ContentQueryPort.class, () -> contents)
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminContentService.class)
                    .hasSingleBean(AdminContentController.class));
  }
}
