package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.infrastructure.persistence.DuPickColumnJpaEntity;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDuPickQueryJpaRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerDuPickTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDuPickQueryJpaRepository jpaRepository;

  @Test
  void getDuPicksReturnsCommonSuccessResponse() throws Exception {
    jpaRepository.saveAndFlush(duPick(1L, "THE ESSENCE IN MOTION", "색과 형태, 우리가 마주한 순간들"));
    jpaRepository.saveAndFlush(duPick(2L, "시선이 머무는 각도", "공간을 채우는 젊은 예술가 인터뷰"));

    mockMvc
        .perform(get("/api/v1/display/du-picks?size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.duPicks[0].duPickId").value(1))
        .andExpect(jsonPath("$.success.data.duPicks[0].title").value("THE ESSENCE IN MOTION"))
        .andExpect(jsonPath("$.success.data.duPicks[0].subtitle").value("색과 형태, 우리가 마주한 순간들"))
        .andExpect(
            jsonPath("$.success.data.duPicks[0].bannerImageUrl")
                .value("https://cdn.displayu.com/home/du_pick_1.png"))
        .andExpect(jsonPath("$.success.data.duPicks[0].authorName").value("에디터 디유"))
        .andExpect(jsonPath("$.success.data.duPicks[0].createdAt").value("2026-06-30"))
        .andExpect(jsonPath("$.success.data.pagination.nextCursor").value(1))
        .andExpect(jsonPath("$.success.data.pagination.size").value(1))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(true))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/du-picks"));
  }

  @Test
  void getDuPicksReturnsBadRequestWhenSizeIsInvalid() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/du-picks?size=101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/du-picks"));
  }

  private static DuPickColumnJpaEntity duPick(Long id, String title, String content) {
    return new DuPickColumnJpaEntity(
        id,
        title,
        content,
        "https://cdn.displayu.com/home/du_pick_" + id + ".png",
        LocalDateTime.of(2026, 6, 30, 11, 0),
        LocalDateTime.of(2026, 6, 30, 11, 0));
  }
}
