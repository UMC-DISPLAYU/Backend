package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayLikeJpaRepository;
import com.example.demo.global.security.JwtFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayLikeControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private SpringDataDisplayLikeJpaRepository displayLikeJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Test
  void likeDisplayCreatesLikeAndReturnsLikeCount() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.likeCount").value(1))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/displays/" + display.getId() + "/likes"));
  }

  @Test
  void likeDisplayReturnsConflictWhenAlreadyLiked() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DUPLICATE_RESOURCE"));
  }

  @Test
  void cancelLikeDisplaySoftDeletesLikeAndReturnsLikeCount() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            delete("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.likeCount").value(0))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/displays/" + display.getId() + "/likes"));

    assertThat(displayLikeJpaRepository.findByDisplayIdAndUserIdValue(display.getId(), 1L))
        .get()
        .extracting("deletedAt")
        .isNotNull();
  }

  @Test
  void cancelLikeDisplayIsIdempotentWhenAlreadyCanceled() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            delete("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            delete("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.likeCount").value(0));
  }

  @Test
  void cancelLikeDisplayReturnsNotFoundWhenLikeDoesNotExist() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            delete("/api/v1/displays/{displayId}/likes", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  @Test
  void likeDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(post("/api/v1/displays/{displayId}/likes", display.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void cancelLikeDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(delete("/api/v1/displays/{displayId}/likes", display.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getDisplayLikeStatusReturnsTrueWhenActiveLikeExists() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    mockMvc
        .perform(
            post("/api/v1/display/like")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(display.getId())))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/isliked", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.isLiked").value(true))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(
            jsonPath("$.meta.path").value("/api/v1/display/" + display.getId() + "/isliked"));
  }

  @Test
  void getDisplayLikeStatusReturnsFalseWhenLikeDoesNotExist() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/isliked", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.isLiked").value(false));
  }

  @Test
  void getDisplayLikeStatusReturnsFalseWhenLikeIsCanceled() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    mockMvc
        .perform(
            post("/api/v1/display/like")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(display.getId())))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            patch("/api/v1/display/like")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(display.getId())))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/isliked", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.isLiked").value(false));
  }

  @Test
  void getDisplayLikeStatusReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(get("/api/v1/display/{displayId}/isliked", display.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getDisplayLikeStatusReturnsNotFoundWhenDisplayDoesNotExist() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/isliked", 999_999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  private static String requestBody(Long displayId) {
    return """
        {
          "displayId": %d
        }
        """
        .formatted(displayId);
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "좋아요 테스트 전시",
        "https://cdn.displayu.com/posters/main.png",
        "부제",
        "전시 설명",
        new DisplayLocation("전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
        "",
        "",
        "중앙대학교",
        "디자인학부",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        DisplayRegion.SEOUL,
        new DisplayPeriod(
            LocalDate.of(2026, 7, 1),
            LocalDate.of(2026, 7, 10),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }
}
