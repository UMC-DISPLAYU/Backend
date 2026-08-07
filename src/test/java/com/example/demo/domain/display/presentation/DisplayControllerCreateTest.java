package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.JwtFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerCreateTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private UserJpaRepository userJpaRepository;

  @Test
  void createDisplayReturnsRegionInDetailResponse() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("SEOUL")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.title").value("FORM 2026"))
        .andExpect(jsonPath("$.success.data.qnaAccount").value("@displayu"))
        .andExpect(jsonPath("$.success.data.contract").value("Instagram DM"))
        .andExpect(jsonPath("$.success.data.region").value("SEOUL"))
        .andExpect(jsonPath("$.success.data.location.latitude").value(37.0063))
        .andExpect(jsonPath("$.success.data.location.longitude").value(127.2267))
        .andExpect(jsonPath("$.success.data.teamMembers[0].userId").value(user.getId()))
        .andExpect(jsonPath("$.success.data.teamMembers[0].displayNickname").value("전시 리더"))
        .andExpect(jsonPath("$.success.data.teamMembers[0].role").value("TEAM_LEADER"))
        .andExpect(jsonPath("$.success.data.teamMembers[0].accepted").value(true))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  @Test
  void createDisplayReturnsBadRequestWhenRegionIsAll() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("ALL")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  @Test
  void createDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/display")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("SEOUL")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  private static String requestBody(String region) {
    return """
        {
          "title": "FORM 2026",
          "posterImageUrl": "https://cdn.displayu.com/posters/form.png",
          "type": "GRADUATION",
          "fields": ["DESIGN", "MEDIA"],
          "region": "%s",
          "schoolOrOrganization": "중앙대학교",
          "departmentOrClub": "디자인학부",
          "qnaAccount": "@displayu",
          "displayNickname": "전시 리더",
          "contract": "Instagram DM",
          "subtitle": "중앙대학교 디자인학부 졸업전시",
          "description": "디자인학부 학생들의 전시입니다.",
          "startDate": "2026-05-28",
          "endDate": "2026-06-05",
          "openTime": "10:00",
          "closeTime": "18:00",
          "locationName": "중앙대학교 안성캠퍼스 301관 대전시실 2층",
          "latitude": 37.0063,
          "longitude": 127.2267,
          "roadAddress": "경기도 안성시 대덕면 서동대로 4726",
          "precautions": "전시장 내 음료 반입 금지"
        }
        """
        .formatted(region);
  }

  private static User user(String name) {
    return User.builder()
        .provider(Provider.Google)
        .providerId("provider-" + name)
        .name(name)
        .nickname(name)
        .socialEmail(name + "@displayu.com")
        .build();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
