package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.result.CommentDeletionResult;
import com.example.demo.domain.admin.application.service.AdminCommentDeletionService;
import com.example.demo.domain.admin.presentation.AdminCommentDeletionController;
import com.example.demo.domain.admin.presentation.mapper.AdminCommentDeletionMapper;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.AuthUser;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

class AdminCommentDeletionTest {
  private static final String PATH = "/api/v1/admin/comments/{commentType}/{commentId}/delete";
  private static final Instant PROCESSED_AT = Instant.parse("2026-11-17T01:00:00Z");
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final CommentDeletionPort comments = mock(CommentDeletionPort.class);
  private final AdminCommentDeletionService service =
      new AdminCommentDeletionService(access, comments);
  private AuthUser operator = new AuthUser(7L);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminCommentDeletionController(
                    service, Mappers.getMapper(AdminCommentDeletionMapper.class)))
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
  @ValueSource(
      strings = {"DISPLAY_REVIEW_REPLY", "ARTWORK_FEELING_REPLY", "PERSONAL_ARTWORK_FEELING_REPLY"})
  void deletesSupportedCommentUsingAuthenticatedOperator(String type) throws Exception {
    when(comments.delete(type, 41L, 7L, "운영 검토"))
        .thenReturn(new CommentDeletionResult(41L, PROCESSED_AT));

    mvc.perform(
            post(PATH, type, 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reason\":\"  운영 검토  \",\"operatorId\":999,\"commentId\":99}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.commentId").value(41))
        .andExpect(jsonPath("$.success.data.processedAt").value(PROCESSED_AT.toString()))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/admin/comments/" + type + "/41/delete"));

    verify(comments).delete(type, 41L, 7L, "운영 검토");
  }

  @ParameterizedTest
  @ValueSource(strings = {"DISPLAY_REVIEW", "ARTWORK_FEELING", "LOUNGE_COMMENT", "comment"})
  void rejectsUnsupportedCommentType(String type) throws Exception {
    mvc.perform(post(PATH, type, 41).contentType(MediaType.APPLICATION_JSON).content(body("사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments);
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
    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
    verifyNoInteractions(comments);
  }

  @Test
  void validatesReasonLengthAfterStrippingWhitespace() throws Exception {
    String validReason = "가".repeat(1000);
    when(comments.delete("DISPLAY_REVIEW_REPLY", 41L, 7L, validReason))
        .thenReturn(new CommentDeletionResult(41L, PROCESSED_AT));

    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("  " + validReason + "  ")))
        .andExpect(status().isOk());
    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("가".repeat(1001))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));

    verify(comments).delete("DISPLAY_REVIEW_REPLY", 41L, 7L, validReason);
    verifyNoMoreInteractions(comments);
  }

  @ParameterizedTest
  @CsvSource({"0,INVALID_INPUT_VALUE", "-1,INVALID_INPUT_VALUE", "invalid,INVALID_TYPE_VALUE"})
  void rejectsInvalidCommentId(String id, String code) throws Exception {
    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value(code));
    verifyNoInteractions(comments);
  }

  @Test
  void rejectsMissingPrincipal() throws Exception {
    operator = null;
    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    verifyNoInteractions(comments);
  }

  @Test
  void rejectsOrdinaryOperatorBeforeValidatingInput() {
    assertThatThrownBy(() -> service.delete(8L, null, null, null))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.FORBIDDEN));
    verifyNoInteractions(comments);
  }

  @Test
  void rejectsNullTypeAndId() {
    assertThatThrownBy(() -> service.delete(7L, null, 41L, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    assertThatThrownBy(() -> service.delete(7L, "DISPLAY_REVIEW_REPLY", null, "사유"))
        .isInstanceOfSatisfying(
            BusinessException.class,
            error -> assertThat(error.errorCode()).isEqualTo(GlobalErrorCode.INVALID_INPUT_VALUE));
    verifyNoInteractions(comments);
  }

  @ParameterizedTest
  @ValueSource(strings = {"NOT_FOUND", "CONFLICT", "INTERNAL_SERVER_ERROR"})
  void preservesDeletionErrors(String code) throws Exception {
    when(comments.delete("DISPLAY_REVIEW_REPLY", 41L, 7L, "사유"))
        .thenThrow(new BusinessException(GlobalErrorCode.valueOf(code)));

    mvc.perform(
            post(PATH, "DISPLAY_REVIEW_REPLY", 41)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("사유")))
        .andExpect(status().is(GlobalErrorCode.valueOf(code).getStatus().value()))
        .andExpect(jsonPath("$.error.code").value(code));
  }

  private String body(String reason) {
    return "{\"reason\":\"" + reason + "\"}";
  }
}
