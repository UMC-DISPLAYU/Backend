package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.result.CommentDetail;
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
  private final AdminCommentService service = new AdminCommentService(access, comments);
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
  void requiresActivationAndBothPortsToRegisterControllerAndService() {
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
        .run(
            ctx ->
                assertThat(ctx)
                    .hasSingleBean(AdminCommentService.class)
                    .hasSingleBean(AdminCommentController.class));
  }
}
