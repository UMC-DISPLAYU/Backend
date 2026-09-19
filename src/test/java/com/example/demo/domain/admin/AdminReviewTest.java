package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.domain.admin.application.port.AdminAccessPort;
import com.example.demo.domain.admin.application.port.DisplayReviewPort;
import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.result.*;
import com.example.demo.domain.admin.application.service.AdminReviewService;
import com.example.demo.domain.admin.infrastructure.config.AdminReviewConfiguration;
import com.example.demo.domain.admin.presentation.AdminReviewController;
import com.example.demo.domain.admin.presentation.mapper.AdminReviewMapper;
import com.example.demo.global.error.*;
import com.example.demo.global.security.AuthUser;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.*;

class AdminReviewTest {
  private final AdminAccessPort access = mock(AdminAccessPort.class);
  private final DisplayReviewPort reviews = mock(DisplayReviewPort.class);
  private final AdminReviewService service = new AdminReviewService(access, reviews);
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(access.isAdmin(7L)).thenReturn(true);
    mvc =
        MockMvcBuilders.standaloneSetup(
                new AdminReviewController(service, Mappers.getMapper(AdminReviewMapper.class)))
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
  void checksAuthorityBeforeEveryOperation() {
    assertThatThrownBy(() -> service.search(8L, new ReviewSearchQuery("PENDING_REVIEW", null, 20)))
        .isInstanceOf(BusinessException.class);
    assertThatThrownBy(() -> service.getDetail(8L, 1L)).isInstanceOf(BusinessException.class);
    assertThatThrownBy(() -> service.approve(8L, 1L)).isInstanceOf(BusinessException.class);
    assertThatThrownBy(() -> service.reject(8L, 1L, "수정 필요")).isInstanceOf(BusinessException.class);
    assertThatThrownBy(() -> service.approve(null, 1L)).isInstanceOf(BusinessException.class);
    verifyNoInteractions(reviews);
  }

  @Test
  void returnsListWithDefaultsAndPagination() throws Exception {
    when(reviews.search(new ReviewSearchQuery("PENDING_REVIEW", null, 20)))
        .thenReturn(
            new ReviewPage(
                List.of(
                    new ReviewSummary(
                        3L,
                        "전시",
                        2L,
                        "PENDING_REVIEW",
                        Instant.parse("2026-09-19T00:00:00Z"),
                        "전시대학교",
                        "전시 대표자",
                        LocalDate.of(2026, 10, 1),
                        LocalDate.of(2026, 10, 7),
                        "https://example.com/poster.png")),
                3L,
                true));
    mvc.perform(get("/api/v1/admin/displays"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].displayId").value(3))
        .andExpect(jsonPath("$.success.data.items[0].requesterId").value(2))
        .andExpect(jsonPath("$.success.data.items[0].school").value("전시대학교"))
        .andExpect(jsonPath("$.success.data.items[0].leaderName").value("전시 대표자"))
        .andExpect(jsonPath("$.success.data.items[0].startDate").value("2026-10-01"))
        .andExpect(jsonPath("$.success.data.items[0].endDate").value("2026-10-07"))
        .andExpect(
            jsonPath("$.success.data.items[0].posterImageUrl")
                .value("https://example.com/poster.png"))
        .andExpect(jsonPath("$.success.data.nextCursor").value(3))
        .andExpect(jsonPath("$.success.data.hasNext").value(true));
  }

  @Test
  void preservesNullableListFieldsAndFilteredCursor() throws Exception {
    ReviewSummary summary =
        new ReviewSummary(
            2L,
            "전시",
            null,
            "REJECTED",
            null,
            "전시대학교",
            null,
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 7),
            null);
    when(reviews.search(new ReviewSearchQuery("REJECTED", 3L, 10)))
        .thenReturn(new ReviewPage(List.of(summary), null, false));
    var mapped = Mappers.getMapper(AdminReviewMapper.class).toResponse(summary);
    assertThat(mapped.requesterId()).isNull();
    assertThat(mapped.leaderName()).isNull();
    assertThat(mapped.posterImageUrl()).isNull();
    mvc.perform(
            get("/api/v1/admin/displays")
                .param("status", "REJECTED")
                .param("cursor", "3")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.items[0].status").value("REJECTED"))
        .andExpect(jsonPath("$.success.data.items[0].leaderName").doesNotExist())
        .andExpect(jsonPath("$.success.data.items[0].posterImageUrl").doesNotExist())
        .andExpect(jsonPath("$.success.data.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.success.data.hasNext").value(false));
    verify(reviews).search(new ReviewSearchQuery("REJECTED", 3L, 10));
  }

  @Test
  void returnsDetailIncludingRejectionReason() throws Exception {
    when(reviews.getDetail(3L))
        .thenReturn(
            new ReviewDetail(
                3L,
                "전시",
                "소제목",
                "내용",
                2L,
                "REJECTED",
                null,
                null,
                7L,
                "일정 수정",
                null,
                null,
                "서울",
                List.of("poster")));
    mvc.perform(get("/api/v1/admin/displays/3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.rejectionReason").value("일정 수정"))
        .andExpect(jsonPath("$.success.data.imageUrls[0]").value("poster"));
  }

  @Test
  void forwardsAuthenticatedReviewerAndReason() throws Exception {
    mvc.perform(post("/api/v1/admin/displays/3/approve")).andExpect(status().isOk());
    mvc.perform(
            post("/api/v1/admin/displays/3/reject")
                .contentType("application/json")
                .content("{\"reason\":\"  일정 수정  \"}"))
        .andExpect(status().isOk());
    verify(reviews).approve(3L, 7L);
    verify(reviews).reject(3L, 7L, "일정 수정");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "\n\t"})
  void rejectsMissingReason(String reason) {
    assertThatThrownBy(() -> service.reject(7L, 3L, reason)).isInstanceOf(BusinessException.class);
    verifyNoInteractions(reviews);
  }

  @Test
  void validatesHttpInputAndDoesNotCallDisplay() throws Exception {
    mvc.perform(
            post("/api/v1/admin/displays/3/reject").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/v1/admin/displays/3/reject")
                .contentType("application/json")
                .content("{\"reason\":\"" + "a".repeat(1001) + "\"}"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/v1/admin/displays").param("size", "101"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/v1/admin/displays").param("status", "DRAFT"))
        .andExpect(status().isBadRequest());
    mvc.perform(get("/api/v1/admin/displays").param("cursor", "0"))
        .andExpect(status().isBadRequest());
    mvc.perform(post("/api/v1/admin/displays/0/approve")).andExpect(status().isBadRequest());
    verifyNoInteractions(reviews);
  }

  @Test
  void rejectsNonAdminAtHttpBoundary() throws Exception {
    when(access.isAdmin(7L)).thenReturn(false);
    mvc.perform(post("/api/v1/admin/displays/3/approve")).andExpect(status().isForbidden());
    verifyNoInteractions(reviews);
  }

  @Test
  void doesNotRegisterControllerByDefault() {
    new ApplicationContextRunner()
        .withUserConfiguration(AdminReviewConfiguration.class, AdminReviewController.class)
        .run(
            context -> {
              assertThat(context).doesNotHaveBean(AdminReviewService.class);
              assertThat(context).doesNotHaveBean(AdminReviewController.class);
            });
  }

  @Test
  void preservesDisplayFailure() throws Exception {
    doThrow(new BusinessException(GlobalErrorCode.CONFLICT)).when(reviews).approve(3L, 7L);
    mvc.perform(post("/api/v1/admin/displays/3/approve"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("CONFLICT"));
  }

  @Test
  void requiresExplicitActivationAndBothAdapters() {
    new ApplicationContextRunner()
        .withUserConfiguration(AdminReviewConfiguration.class)
        .run(context -> assertThat(context).doesNotHaveBean(AdminReviewService.class));
    new ApplicationContextRunner()
        .withUserConfiguration(AdminReviewConfiguration.class)
        .withPropertyValues("admin.review.enabled=true")
        .run(context -> assertThat(context).hasFailed());
    new ApplicationContextRunner()
        .withUserConfiguration(AdminReviewConfiguration.class)
        .withPropertyValues("admin.review.enabled=true")
        .withBean(AdminAccessPort.class, () -> access)
        .withBean(DisplayReviewPort.class, () -> reviews)
        .run(context -> assertThat(context).hasSingleBean(AdminReviewService.class));
  }
}
