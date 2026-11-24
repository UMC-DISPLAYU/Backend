package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.CommentReportQueryPort;
import com.example.demo.domain.admin.application.query.CommentReportSearchQuery;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.CommentReportPage;
import com.example.demo.domain.admin.application.service.AdminCommentService;
import com.example.demo.domain.admin.infrastructure.config.AdminCommentConfiguration;
import com.example.demo.domain.admin.presentation.AdminCommentController;
import com.example.demo.domain.admin.presentation.mapper.AdminCommentMapper;
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
import org.mockito.InOrder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminCommentTest {
  private static final Instant CREATED_AT = Instant.parse("2026-11-02T12:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final CommentQueryPort comments = mock(CommentQueryPort.class);
  private final CommentReportQueryPort reports = mock(CommentReportQueryPort.class);
  private final AdminCommentService service = new AdminCommentService(access, comments, reports);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminCommentController(service, Mappers.getMapper(AdminCommentMapper.class)))
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
  void returnsCommentParentAndContextWithoutChangingDomainCodes() throws Exception {
    var attachment = new CommentDetail.Attachment("https://example.com/image.jpg", "IMAGE");
    var comment =
        new CommentDetail.Comment(
            "DISPLAY_REVIEW_REPLY",
            41L,
            11L,
            "댓글 작성자",
            "댓글 원문",
            List.of(attachment),
            CREATED_AT,
            null);
    var parent =
        new CommentDetail.Parent(
            "DISPLAY_REVIEW", 31L, 12L, "후기 작성자", "후기 원문", List.of(), CREATED_AT, null);
    when(comments.getCommentDetail("DISPLAY_REVIEW_REPLY", 41L))
        .thenReturn(new CommentDetail(comment, parent, 21L, null));

    mvc.perform(get("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.comment.commentType").value("DISPLAY_REVIEW_REPLY"))
        .andExpect(jsonPath("$.success.data.comment.commentId").value(41))
        .andExpect(jsonPath("$.success.data.comment.authorUserId").value(11))
        .andExpect(jsonPath("$.success.data.comment.authorNickname").value("댓글 작성자"))
        .andExpect(jsonPath("$.success.data.comment.content").value("댓글 원문"))
        .andExpect(
            jsonPath("$.success.data.comment.attachments[0].url")
                .value("https://example.com/image.jpg"))
        .andExpect(jsonPath("$.success.data.comment.attachments[0].type").value("IMAGE"))
        .andExpect(jsonPath("$.success.data.comment.createdAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.comment.deletedAt").value(nullValue()))
        .andExpect(jsonPath("$.success.data.parent.parentType").value("DISPLAY_REVIEW"))
        .andExpect(jsonPath("$.success.data.parent.parentId").value(31))
        .andExpect(jsonPath("$.success.data.parent.attachments").isEmpty())
        .andExpect(jsonPath("$.success.data.displayId").value(21))
        .andExpect(jsonPath("$.success.data.artworkId").value(nullValue()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41"));
  }

  @Test
  void returnsRetainedDeletedCommentWithWithdrawnAuthorAndNoParent() throws Exception {
    var comment =
        new CommentDetail.Comment(
            "LOUNGE_COMMENT", 41L, null, null, null, List.of(), CREATED_AT, CREATED_AT);
    when(comments.getCommentDetail("LOUNGE_COMMENT", 41L))
        .thenReturn(new CommentDetail(comment, null, null, null));

    mvc.perform(get("/api/v1/admin/comments/LOUNGE_COMMENT/41"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.comment.authorUserId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.comment.authorNickname").value(nullValue()))
        .andExpect(jsonPath("$.success.data.comment.content").value(nullValue()))
        .andExpect(jsonPath("$.success.data.comment.attachments").isEmpty())
        .andExpect(jsonPath("$.success.data.comment.deletedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.parent").value(nullValue()));
  }

  @Test
  void returnsReportsForRetainedDeletedCommentWithStableCursor() throws Exception {
    var deletedComment =
        new CommentDetail.Comment(
            "DISPLAY_REVIEW_REPLY", 41L, 11L, "작성자", "삭제된 댓글", List.of(), CREATED_AT, CREATED_AT);
    var query = new CommentReportSearchQuery(null, 20);
    when(comments.getCommentDetail("DISPLAY_REVIEW_REPLY", 41L))
        .thenReturn(new CommentDetail(deletedComment, null, null, null));
    when(reports.search("DISPLAY_REVIEW_REPLY", 41L, query))
        .thenReturn(
            new CommentReportPage(
                List.of(
                    new CommentReportPage.Item(91L, 21L, "OTHER", "상세 사유", CREATED_AT),
                    new CommentReportPage.Item(90L, 22L, "ABUSE_DEFAMATION", null, CREATED_AT)),
                90L,
                true));

    mvc.perform(get("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41/reports"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].reportId").value(91))
        .andExpect(jsonPath("$.success.data.items[0].reporterUserId").value(21))
        .andExpect(jsonPath("$.success.data.items[0].reasonCode").value("OTHER"))
        .andExpect(jsonPath("$.success.data.items[0].description").value("상세 사유"))
        .andExpect(jsonPath("$.success.data.items[0].reportedAt").value(CREATED_AT.toString()))
        .andExpect(jsonPath("$.success.data.items[1].description").value(nullValue()))
        .andExpect(jsonPath("$.success.data.nextCursor").value(90))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));

    InOrder order = inOrder(comments, reports);
    order.verify(comments).getCommentDetail("DISPLAY_REVIEW_REPLY", 41L);
    order.verify(reports).search("DISPLAY_REVIEW_REPLY", 41L, query);
  }

  @Test
  void passesReportCursorAndBoundarySizes() throws Exception {
    when(reports.search(anyString(), anyLong(), any()))
        .thenReturn(new CommentReportPage(List.of(), null, false));
    for (int size : List.of(1, 100)) {
      mvc.perform(
              get("/api/v1/admin/comments/ARTWORK_FEELING_REPLY/41/reports")
                  .param("cursor", "50")
                  .param("size", String.valueOf(size)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.items").isEmpty())
          .andExpect(jsonPath("$.success.data.nextCursor").value(nullValue()))
          .andExpect(jsonPath("$.success.data.hasNext").value(false));
      verify(reports).search("ARTWORK_FEELING_REPLY", 41L, new CommentReportSearchQuery(50L, size));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"DISPLAY_REVIEW", "UNKNOWN", "display_review_reply", "COMMENT-TYPE"})
  void rejectsUnsupportedReportCommentTypes(String commentType) throws Exception {
    mvc.perform(get("/api/v1/admin/comments/" + commentType + "/41/reports"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments, reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"?cursor=0", "?cursor=-1", "?size=0", "?size=101"})
  void rejectsInvalidReportPagination(String queryString) throws Exception {
    mvc.perform(get("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41/reports" + queryString))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments, reports);
  }

  @Test
  void doesNotQueryReportsWhenCommentDoesNotExist() throws Exception {
    when(comments.getCommentDetail("DISPLAY_REVIEW_REPLY", 41L))
        .thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));

    mvc.perform(get("/api/v1/admin/comments/DISPLAY_REVIEW_REPLY/41/reports"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    verifyNoInteractions(reports);
  }

  @ParameterizedTest
  @ValueSource(strings = {"comment", "1COMMENT", "COMMENT-TYPE", "COMMENT.TYPE"})
  void rejectsInvalidCommentTypeBeforeCallingPort(String commentType) throws Exception {
    mvc.perform(get("/api/v1/admin/comments/" + commentType + "/41"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments);
  }

  @ParameterizedTest
  @ValueSource(strings = {"0", "-1"})
  void rejectsNonPositiveCommentIdBeforeCallingPort(String commentId) throws Exception {
    mvc.perform(get("/api/v1/admin/comments/COMMENT_KIND/" + commentId))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments);
  }

  @Test
  void rejectsNonNumericCommentId() throws Exception {
    mvc.perform(get("/api/v1/admin/comments/COMMENT_KIND/invalid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_TYPE_VALUE"));
    verifyNoInteractions(comments);
  }

  @Test
  void propagatesMissingCommentAsCommonNotFoundError() throws Exception {
    when(comments.getCommentDetail("COMMENT_KIND", 41L))
        .thenThrow(new BusinessException(GlobalErrorCode.NOT_FOUND));

    mvc.perform(get("/api/v1/admin/comments/COMMENT_KIND/41"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(longs = {8L})
  void rejectsUnauthorizedCallersBeforeQueryingComments(Long adminId) {
    GlobalErrorCode error =
        adminId == null ? GlobalErrorCode.UNAUTHORIZED : GlobalErrorCode.FORBIDDEN;
    assertThatThrownBy(() -> service.getDetail(adminId, "COMMENT_KIND", 41L))
        .isInstanceOfSatisfying(
            BusinessException.class, ex -> assertThat(ex.errorCode()).isEqualTo(error));
    verifyNoInteractions(comments);
  }

  @Test
  void rejectsNullInputsBeforeCallingPort() {
    assertThatThrownBy(() -> service.getDetail(7L, null, 41L))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.getDetail(7L, "COMMENT_KIND", null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            ex -> assertThat(ex.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(comments);
  }

  @Test
  void requiresActivationAndAllPortsToRegisterControllerAndService() {
    ApplicationContextRunner context =
        new ApplicationContextRunner()
            .withUserConfiguration(AdminCommentConfiguration.class, AdminCommentController.class)
            .withBean(AdminCommentMapper.class, () -> Mappers.getMapper(AdminCommentMapper.class));
    context.run(
        ctx ->
            assertThat(ctx)
                .doesNotHaveBean(AdminCommentService.class)
                .doesNotHaveBean(AdminCommentController.class));
    context
        .withPropertyValues("admin.comments.enabled=false")
        .run(
            ctx ->
                assertThat(ctx)
                    .doesNotHaveBean(AdminCommentService.class)
                    .doesNotHaveBean(AdminCommentController.class));
    context
        .withPropertyValues("admin.comments.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.comments.enabled=true")
        .withBean(CommentQueryPort.class, () -> comments)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.comments.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(CommentQueryPort.class, () -> comments)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.comments.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(CommentReportQueryPort.class, () -> reports)
        .run(ctx -> assertThat(ctx).hasFailed());
    context
        .withPropertyValues("admin.comments.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(CommentQueryPort.class, () -> comments)
        .withBean(CommentReportQueryPort.class, () -> reports)
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminCommentService.class)
                    .hasSingleBean(AdminCommentController.class));
  }
}
