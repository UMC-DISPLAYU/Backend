package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
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
        .andExpect(jsonPath("$.success.data.location.roadAddress").value("경기도 안성시 대덕면 서동대로 4726"))
        .andExpect(jsonPath("$.success.data.teamMembers[0].userId").value(user.getId()))
        .andExpect(jsonPath("$.success.data.teamMembers[0].displayNickname").value("전시 리더"))
        .andExpect(jsonPath("$.success.data.teamMembers[0].role").value("TEAM_LEADER"))
        .andExpect(jsonPath("$.success.data.teamMembers[0].accepted").value(true))
        .andExpect(jsonPath("$.success.data.images.length()").value(3))
        .andExpect(
            jsonPath("$.success.data.images[0].imageUrl")
                .value("https://cdn.displayu.com/posters/form.png"))
        .andExpect(jsonPath("$.success.data.images[0].imageType").value("MAIN"))
        .andExpect(jsonPath("$.success.data.images[0].sortOrder").value(0))
        .andExpect(
            jsonPath("$.success.data.images[1].imageUrl")
                .value("https://cdn.displayu.com/display/detail-1.png"))
        .andExpect(jsonPath("$.success.data.images[1].imageType").value("DETAIL"))
        .andExpect(jsonPath("$.success.data.images[1].sortOrder").value(0))
        .andExpect(
            jsonPath("$.success.data.images[2].imageUrl")
                .value("https://cdn.displayu.com/display/detail-2.png"))
        .andExpect(jsonPath("$.success.data.images[2].imageType").value("DETAIL"))
        .andExpect(jsonPath("$.success.data.images[2].sortOrder").value(1))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  @Test
  void createDisplaySucceedsWithSmallGroupTypeAndIllustrationField() throws Exception {
    User user = userJpaRepository.save(user("소모임장"));
    String request =
        requestBody("SEOUL")
            .replace("\"GRADUATION\"", "\"SMALL_GROUP\"")
            .replace("[\"DESIGN\", \"VIDEO\"]", "[\"ILLUSTRATION\"]");

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.displayType").value("SMALL_GROUP"));
  }

  @Test
  void createDisplayRejectsLegacyInterdisciplinaryField() throws Exception {
    User user = userJpaRepository.save(user("소모임장"));
    String request =
        requestBody("SEOUL").replace("[\"DESIGN\", \"VIDEO\"]", "[\"INTERDISCIPLINARY\"]");

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST_BODY"));
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
  void createDisplayReturnsBadRequestWhenDisplayImageUrlHasMoreThanFourItems() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyWithFiveDisplayImages()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  @Test
  void createDisplaySucceedsWithoutQnaAccount() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyWithoutQnaAccount()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.qnaAccount").value(""));
  }

  @Test
  void createDisplaySucceedsWithoutContract() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyWithoutContract()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.contract").doesNotExist());
  }

  @Test
  void createDisplayReturnsConflictWhenSameOwnerAlreadyHasSameTitle() throws Exception {
    User user = userJpaRepository.save(user("홍길동"));

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("SEOUL")))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody("SEOUL")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_ALREADY_EXISTS"))
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
          "displayImageUrl": [
            "https://cdn.displayu.com/display/detail-1.png",
            "https://cdn.displayu.com/display/detail-2.png"
          ],
          "type": "GRADUATION",
          "fields": ["DESIGN", "VIDEO"],
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

  private static String requestBodyWithoutQnaAccount() {
    return """
        {
          "title": "FORM 2026",
          "posterImageUrl": "https://cdn.displayu.com/posters/form.png",
          "displayImageUrl": [
            "https://cdn.displayu.com/display/detail-1.png",
            "https://cdn.displayu.com/display/detail-2.png"
          ],
          "type": "GRADUATION",
          "fields": ["DESIGN", "VIDEO"],
          "region": "SEOUL",
          "schoolOrOrganization": "중앙대학교",
          "departmentOrClub": "디자인학부",
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
        """;
  }

  private static String requestBodyWithoutContract() {
    return """
        {
          "title": "FORM 2026",
          "posterImageUrl": "https://cdn.displayu.com/posters/form.png",
          "displayImageUrl": [
            "https://cdn.displayu.com/display/detail-1.png",
            "https://cdn.displayu.com/display/detail-2.png"
          ],
          "type": "GRADUATION",
          "fields": ["DESIGN", "VIDEO"],
          "region": "SEOUL",
          "schoolOrOrganization": "중앙대학교",
          "departmentOrClub": "디자인학부",
          "qnaAccount": "@displayu",
          "displayNickname": "전시 리더",
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
        """;
  }

  private static String requestBodyWithFiveDisplayImages() {
    return """
        {
          "title": "FORM 2026",
          "posterImageUrl": "https://cdn.displayu.com/posters/form.png",
          "displayImageUrl": [
            "https://cdn.displayu.com/display/detail-1.png",
            "https://cdn.displayu.com/display/detail-2.png",
            "https://cdn.displayu.com/display/detail-3.png",
            "https://cdn.displayu.com/display/detail-4.png",
            "https://cdn.displayu.com/display/detail-5.png"
          ],
          "type": "GRADUATION",
          "fields": ["DESIGN", "VIDEO"],
          "region": "SEOUL",
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
        """;
  }

  private static User user(String name) {
    return User.builder()
        .provider(Provider.Google)
        .providerId("provider-" + name)
        .name(name)
        .nickname(name)
        .socialEmail(name + "@displayu.com")
        .isVerified(true)
        .build();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
