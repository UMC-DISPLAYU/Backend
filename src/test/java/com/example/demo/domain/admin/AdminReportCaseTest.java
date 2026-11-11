package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.ReportCaseQueryPort;
import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;
import com.example.demo.domain.admin.application.result.ReportCaseSummary;
import com.example.demo.domain.admin.application.service.AdminReportCaseService;
import com.example.demo.domain.admin.infrastructure.config.AdminReportCaseConfiguration;
import com.example.demo.domain.admin.presentation.AdminReportCaseController;
import com.example.demo.domain.admin.presentation.mapper.AdminReportCaseMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminReportCaseTest {
  private static final Instant CREATED_AT = Instant.parse("2026-11-02T12:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final ReportCaseQueryPort reports = mock(ReportCaseQueryPort.class);
  private final AdminReportCaseService service = new AdminReportCaseService(access, reports);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminReportCaseController(
                    service, Mappers.getMapper(AdminReportCaseMapper.class)))
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
    when(reports.search(new ReportCaseSearchQuery(null, null, 20)))
        .thenReturn(
            new ReportCasePage(
                List.of(
                    new ReportCaseSummary(301L, "PENDING", "COMMENT_KIND", 401L, 2L, CREATED_AT)),
                301L,
                true));
    mvc.perform(get("/api/v1/admin/report-cases"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].reportCaseId").value(301))
        .andExpect(jsonPath("$.success.data.items[0].commentType").value("COMMENT_KIND"))
        .andExpect(jsonPath("$.success.data.items[0].commentId").value(401))
        .andExpect(jsonPath("$.success.data.items[0].reportCount").value(2))
        .andExpect(jsonPath("$.success.data.items[0].lastReportedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.nextCursor").value(301))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));
    verify(reports).search(new ReportCaseSearchQuery("PENDING", null, 20));
  }

  @Test
  void passesResolvedFilterAndBoundaryValues() throws Exception {
    when(reports.search(any())).thenReturn(new ReportCasePage(List.of(), null, false));
    for (String size : List.of("1", "100")) {
      mvc.perform(
              get("/api/v1/admin/report-cases")
                  .param("status", "RESOLVED")
                  .param("cursor", "1")
                  .param("size", size))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.items").isEmpty())
          .andExpect(jsonPath("$.success.data.nextCursor").value(nullValue()))
          .andExpect(jsonPath("$.success.data.hasNext").value(false));
      verify(reports).search(new ReportCaseSearchQuery("RESOLVED", 1L, Integer.parseInt(size)));
    }
  }

  @ParameterizedTest
  @CsvSource({
    "cursor,0",
    "cursor,-1",
    "size,0",
    "size,101",
    "status,UNKNOWN",
    "status,pending",
    "status,''"
  })
  void rejectsInvalidFiltersWithoutCallingPort(String parameter, String value) throws Exception {
    mvc.perform(get("/api/v1/admin/report-cases").param(parameter, value))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"cursor", "size"})
  void rejectsNonNumericQueryValues(String parameter) throws Exception {
    mvc.perform(get("/api/v1/admin/report-cases").param(parameter, "invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(reports);
  }

  @Test
  void mapsCurrentCommentParentReportsAndResolutionWithoutInferringCodes() throws Exception {
    var attachment = new ReportCaseDetail.Attachment("https://example.com/image", "IMAGE");
    var comment =
        new ReportCaseDetail.Comment(
            "COMMENT_KIND", 401L, null, null, null, List.of(), CREATED_AT, CREATED_AT);
    var parent =
        new ReportCaseDetail.Parent(
            "PARENT_KIND", 11L, 12L, "작성자", "부모 원문", List.of(attachment), CREATED_AT, null);
    var report = new ReportCaseDetail.Report(601L, null, "REASON_CODE", null, CREATED_AT);
    var resolution = new ReportCaseDetail.Resolution("NO_ACTION", "검토", CREATED_AT, 7L);
    when(reports.getDetail(301L))
        .thenReturn(
            new ReportCaseDetail(
                301L,
                "RESOLVED",
                "COMMENT_KIND",
                401L,
                1L,
                CREATED_AT,
                comment,
                parent,
                101L,
                null,
                List.of(report),
                resolution));
    mvc.perform(get("/api/v1/admin/report-cases/301"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comment.content").value(nullValue()))
        .andExpect(jsonPath("$.success.data.comment.deletedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.comment.attachments").isEmpty())
        .andExpect(jsonPath("$.success.data.parent.parentType").value("PARENT_KIND"))
        .andExpect(jsonPath("$.success.data.parent.attachments[0].url").value(attachment.url()))
        .andExpect(jsonPath("$.success.data.parent.attachments[0].type").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.reports[0].reportId").value(601))
        .andExpect(jsonPath("$.success.data.reports[0].reporterUserId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.reports[0].reasonCode").value("REASON_CODE"))
        .andExpect(jsonPath("$.success.data.resolution.operatorId").value(7))
        .andExpect(jsonPath("$.success.data.resolution.action").value("NO_ACTION"))
        .andExpect(jsonPath("$.success.data.artworkId").value(nullValue()));
  }

  @Test
  void pendingCaseHasNoResolutionAndMayHaveNoRetainedComment() throws Exception {
    when(reports.getDetail(301L))
        .thenReturn(
            new ReportCaseDetail(
                301L,
                "PENDING",
                "COMMENT_KIND",
                401L,
                0L,
                null,
                null,
                null,
                null,
                null,
                List.of(),
                null));
    mvc.perform(get("/api/v1/admin/report-cases/301"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comment").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parent").value(nullValue()))
        .andExpect(jsonPath("$.success.data.reports").isEmpty())
        .andExpect(jsonPath("$.success.data.resolution").value(nullValue()));
  }

  @Test
  void propagatesMissingCaseAsCommonNotFoundError() throws Exception {
    when(reports.getDetail(10L)).thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));

    mvc.perform(get("/api/v1/admin/report-cases/10"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.success").value(nullValue()))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/report-cases/10"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1"})
  void rejectsNonPositiveTargetIds(String id) throws Exception {
    mvc.perform(get("/api/v1/admin/report-cases/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(reports);
  }

  @Test
  void rejectsNonNumericTargetId() throws Exception {
    mvc.perform(get("/api/v1/admin/report-cases/invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(reports);
  }

  @Test
  void rejectsNullTargetIdBeforeCallingPort() {
    assertThatThrownBy(() -> service.getDetail(7L, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {8L})
  void rejectsUnauthorizedCallersBeforeQueryingReports(Long adminId) {
    GlobalErrorCode error =
        adminId == null ? GlobalErrorCode.UNAUTHORIZED : GlobalErrorCode.FORBIDDEN;
    assertThatThrownBy(() -> service.search(adminId, new ReportCaseSearchQuery(null, null, 20)))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    assertThatThrownBy(() -> service.getDetail(adminId, 10L))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    verifyNoInteractions(reports);
  }

  @Test
  void requiresActivationAndBothPortsToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(
                AdminReportCaseConfiguration.class, AdminReportCaseController.class)
            .withBean(
                AdminReportCaseMapper.class, () -> Mappers.getMapper(AdminReportCaseMapper.class));
    context.run(
        ctx -> {
          assertThat(ctx).hasNotFailed();
          assertThat(ctx)
              .doesNotHaveBean(AdminReportCaseService.class)
              .doesNotHaveBean(AdminReportCaseController.class);
        });
    context
        .withPropertyValues("admin.report-cases.enabled=false")
        .run(
            ctx -> {
              assertThat(ctx).hasNotFailed();
              assertThat(ctx)
                  .doesNotHaveBean(AdminReportCaseService.class)
                  .doesNotHaveBean(AdminReportCaseController.class);
            });
    context
        .withPropertyValues("admin.report-cases.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.report-cases.enabled=true")
        .withBean(ReportCaseQueryPort.class, () -> reports)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.report-cases.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(ReportCaseQueryPort.class, () -> reports)
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminReportCaseService.class)
                    .hasSingleBean(AdminReportCaseController.class));
  }
}
