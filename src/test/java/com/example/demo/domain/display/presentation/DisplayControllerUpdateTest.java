package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class DisplayControllerUpdateTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private EntityManager entityManager;

  @Test
  void getDraftDisplayDetailSucceedsWhenRequesterIsAcceptedTeamMember() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.status").value("DRAFT"));
  }

  @Test
  void updateDisplayUpdatesOptionalFieldsWhenRequesterIsTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.title").value("FORM 2026 (수정본)"))
        .andExpect(jsonPath("$.success.data.subtitle").value("변경된 전시 부제목입니다."))
        .andExpect(jsonPath("$.success.data.content").value("변경된 전시 소개글입니다."))
        .andExpect(jsonPath("$.success.data.contract").value("Email"))
        .andExpect(jsonPath("$.success.data.note").value("물품 보관소를 운영하지 않습니다."))
        .andExpect(jsonPath("$.success.data.location.placeName").value("중앙대학교 301관 갤러리 3층 전시장"))
        .andExpect(jsonPath("$.success.data.displayType").value("GRADUATION"))
        .andExpect(jsonPath("$.success.data.displayFields[0]").value("DESIGN"))
        .andExpect(jsonPath("$.success.data.displayFields[1]").value("VIDEO"))
        .andExpect(jsonPath("$.success.data.period.startDate").value("2026-05-29"))
        .andExpect(jsonPath("$.success.data.period.endDate").value("2026-06-06"))
        .andExpect(jsonPath("$.success.data.period.startTime").value("09:00:00"))
        .andExpect(jsonPath("$.success.data.period.endTime").value("19:00:00"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display"));
  }

  @Test
  void updateDisplayReturnsForbiddenWhenRequesterIsNotTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void updateDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void updatesLinksLocationAndDetailImagesAndPersistsThem() throws Exception {
    Display display = displayWithTeamMembers();
    display.changeDetailImages(List.of("https://example.com/old.png"));
    displayJpaRepository.saveAndFlush(display);
    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"displayId": %d, "instagramUrl": "https://instagram.com/new",
             "websiteUrl": "https://example.com", "roadAddress": "새 주소",
             "latitude": 35.1, "longitude": 129.1, "region": "OTHERS",
             "displayImageUrl": ["https://example.com/1.png", "https://example.com/2.png"]}
            """
                        .formatted(display.getId())))
        .andExpect(status().isOk());
    displayJpaRepository.flush();
    entityManager.clear();
    mockMvc
        .perform(
            get("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.instagramUrl").value("https://instagram.com/new"))
        .andExpect(jsonPath("$.success.data.websiteUrl").value("https://example.com"))
        .andExpect(jsonPath("$.success.data.location.roadAddress").value("새 주소"))
        .andExpect(jsonPath("$.success.data.location.latitude").value(35.1))
        .andExpect(jsonPath("$.success.data.location.longitude").value(129.1))
        .andExpect(jsonPath("$.success.data.location.placeName").value("기존 전시장"))
        .andExpect(jsonPath("$.success.data.region").value("OTHERS"))
        .andExpect(jsonPath("$.success.data.images.length()").value(3))
        .andExpect(
            jsonPath("$.success.data.images[?(@.imageType == 'DETAIL')].imageUrl")
                .value(
                    org.hamcrest.Matchers.contains(
                        "https://example.com/1.png", "https://example.com/2.png")));
  }

  @Test
  void omittedFieldsAreKeptAndExplicitEmptyValuesClearLinksAndDetails() throws Exception {
    Display display = displayWithTeamMembers();
    display.changeExternalLinks("https://instagram.com/old", "https://example.com");
    display.changeDetailImages(List.of("https://example.com/old.png"));
    displayJpaRepository.saveAndFlush(display);
    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayId\": %d, \"instagramUrl\": null}".formatted(display.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.instagramUrl").value("https://instagram.com/old"))
        .andExpect(jsonPath("$.success.data.images.length()").value(2));
    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
            {"displayId": %d, "instagramUrl": "", "displayImageUrl": []}
            """
                        .formatted(display.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.instagramUrl").doesNotExist())
        .andExpect(jsonPath("$.success.data.websiteUrl").value("https://example.com"))
        .andExpect(jsonPath("$.success.data.images.length()").value(1))
        .andExpect(jsonPath("$.success.data.images[0].imageType").value("MAIN"));
    displayJpaRepository.flush();
    entityManager.clear();
    mockMvc
        .perform(
            get("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.instagramUrl").doesNotExist())
        .andExpect(jsonPath("$.success.data.websiteUrl").value("https://example.com"))
        .andExpect(jsonPath("$.success.data.images.length()").value(1))
        .andExpect(jsonPath("$.success.data.images[0].imageType").value("MAIN"));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "\"instagramUrl\": \"ftp://example.com\"",
        "\"websiteUrl\": \" \"",
        "\"latitude\": 91",
        "\"longitude\": -181",
        "\"region\": \"ALL\"",
        "\"roadAddress\": \" \"",
        "\"displayImageUrl\": [\"1\", \"2\", \"3\", \"4\", \"5\"]",
        "\"displayImageUrl\": [\"\"]"
      })
  void rejectsInvalidNewFields(String field) throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);
    mockMvc
        .perform(
            patch("/api/v1/display")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayId\": %d, %s}".formatted(display.getId(), field)))
        .andExpect(status().isBadRequest());
  }

  private static String updateRequest(Long displayId) {
    return """
        {
          "displayId": %d,
          "title": "FORM 2026 (수정본)",
          "posterImageUrl": "https://cdn.displayu.com/posters/updated.png",
          "type": "GRADUATION",
          "fields": ["DESIGN", "VIDEO"],
          "schoolOrOrganization": "중앙대학교",
          "departmentOrClub": "디자인학부 시각디자인",
          "hostOrganizationName": null,
          "contract": "Email",
          "subtitle": "변경된 전시 부제목입니다.",
          "description": "변경된 전시 소개글입니다.",
          "startDate": "2026-05-29",
          "endDate": "2026-06-06",
          "openTime": "09:00",
          "closeTime": "19:00",
          "placeName": "중앙대학교 301관 갤러리 3층 전시장",
          "precautions": "물품 보관소를 운영하지 않습니다."
        }
        """
        .formatted(displayId);
  }

  private static Display displayWithTeamMembers() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "기존 부제",
            "기존 설명",
            new DisplayLocation(
                "기존 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "기존 유의사항",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
