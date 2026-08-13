package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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
class DisplayContentControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Test
  void createCategorySucceedsWhenRequesterIsAcceptedTeamMember() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("전시장 전경", "전시장 이미지입니다.")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.categoryId").isNumber())
        .andExpect(jsonPath("$.success.data.name").value("전시장 전경"))
        .andExpect(jsonPath("$.success.data.sortOrder").value(0))
        .andExpect(jsonPath("$.success.data.contents").isArray())
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(
            jsonPath("$.meta.path")
                .value("/api/v1/display/" + display.getId() + "/content-categories"));
  }

  @Test
  void createCategoryReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", display.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("전시장 전경", "전시장 이미지입니다.")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void createCategoryReturnsForbiddenWhenRequesterIsNotAcceptedTeamMember() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(3L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("전시장 전경", "전시장 이미지입니다.")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_PERMISSION_DENIED"));
  }

  @Test
  void createCategoryReturnsForbiddenWhenRequesterIsPendingTeamMember() throws Exception {
    Display display = display();
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "대기 팀원", TeamMemberRole.TEAM_MEM, false));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", savedDisplay.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("전시장 전경", "전시장 이미지입니다.")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_PERMISSION_DENIED"));
  }

  @Test
  void createCategoryReturnsBadRequestWhenNameIsBlank() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("", "전시장 이미지입니다.")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void createCategorySucceedsWithoutDescription() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/content-categories", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequestWithoutDescription("전시장 전경")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.name").value("전시장 전경"))
        .andExpect(jsonPath("$.success.data.description").value(""));
  }

  @Test
  void updateCategoryReturnsNotFoundWhenCategoryDoesNotExist() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}",
                    display.getId(),
                    999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryRequest("메인 전시장", "메인 전시장 이미지입니다.")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_CATEGORY_NOT_FOUND"));
  }

  @Test
  void deleteCategoryRemovesCategoryAndContents() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long categoryId = savedDisplay.getContentCategories().getFirst().getId();

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}",
                    savedDisplay.getId(),
                    categoryId)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(savedDisplay.getId()))
        .andExpect(jsonPath("$.success.data.categoryId").value(categoryId));

    assertThat(savedDisplay.getContentCategories()).isEmpty();
  }

  @Test
  void createContentSucceedsAndAssignsNextSortOrder() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long categoryId = savedDisplay.getContentCategories().getFirst().getId();

    mockMvc
        .perform(
            post(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents",
                    savedDisplay.getId(),
                    categoryId)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-2.jpg")))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.categoryId").value(categoryId))
        .andExpect(jsonPath("$.success.data.contentId").isNumber())
        .andExpect(
            jsonPath("$.success.data.imageUrl")
                .value("https://cdn.displayu.com/display/content-2.jpg"))
        .andExpect(jsonPath("$.success.data.sortOrder").value(1))
        .andExpect(jsonPath("$.error").doesNotExist());
  }

  @Test
  void createContentReturnsBadRequestWhenCategoryHasTwentyContents() throws Exception {
    Display display = displayWithFullCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long categoryId = savedDisplay.getContentCategories().getFirst().getId();

    mockMvc
        .perform(
            post(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents",
                    savedDisplay.getId(),
                    categoryId)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-21.jpg")))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_LIMIT_EXCEEDED"));
  }

  @Test
  void createContentReturnsNotFoundWhenCategoryDoesNotExist() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(displayWithMember());

    mockMvc
        .perform(
            post(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents",
                    display.getId(),
                    999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-1.jpg")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_CATEGORY_NOT_FOUND"));
  }

  @Test
  void updateContentReturnsNotFoundWhenContentDoesNotExist() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    Long categoryId = savedDisplay.getContentCategories().getFirst().getId();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/{contentId}",
                    savedDisplay.getId(),
                    categoryId,
                    999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-updated.jpg")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_NOT_FOUND"));
  }

  @Test
  void updateContentSucceedsWhenRequesterIsAcceptedTeamMemberOnDraftDisplay() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long contentId = category.getContents().getFirst().getId();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/{contentId}",
                    savedDisplay.getId(),
                    category.getId(),
                    contentId)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-updated.jpg")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.contentId").value(contentId))
        .andExpect(
            jsonPath("$.success.data.imageUrl")
                .value("https://cdn.displayu.com/display/content-updated.jpg"));
  }

  @Test
  void updateContentReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long contentId = category.getContents().getFirst().getId();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/{contentId}",
                    savedDisplay.getId(),
                    category.getId(),
                    contentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentRequest("https://cdn.displayu.com/display/content-updated.jpg")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void deleteContentSucceedsWhenRequesterIsAcceptedTeamMemberOnDraftDisplay() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long contentId = category.getContents().getFirst().getId();

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/{contentId}",
                    savedDisplay.getId(),
                    category.getId(),
                    contentId)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(savedDisplay.getId()))
        .andExpect(jsonPath("$.success.data.categoryId").value(category.getId()))
        .andExpect(jsonPath("$.success.data.contentId").value(contentId));

    assertThat(category.getContents()).isEmpty();
  }

  @Test
  void deleteContentReturnsForbiddenWhenRequesterIsNotAcceptedTeamMember() throws Exception {
    Display display = displayWithCategory();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long contentId = category.getContents().getFirst().getId();

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/{contentId}",
                    savedDisplay.getId(),
                    category.getId(),
                    contentId)
                .header(HttpHeaders.AUTHORIZATION, bearer(3L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_PERMISSION_DENIED"));
  }

  @Test
  void reorderContentsSucceedsAndReturnsUpdatedSortOrders() throws Exception {
    Display display = displayWithThreeContents();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long firstId = category.getContents().get(0).getId();
    Long secondId = category.getContents().get(1).getId();
    Long thirdId = category.getContents().get(2).getId();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/reorder",
                    savedDisplay.getId(),
                    category.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reorderRequest(List.of(thirdId, firstId, secondId))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.contents[0].contentId").value(thirdId))
        .andExpect(jsonPath("$.success.data.contents[0].sortOrder").value(0))
        .andExpect(jsonPath("$.success.data.contents[1].contentId").value(firstId))
        .andExpect(jsonPath("$.success.data.contents[1].sortOrder").value(1))
        .andExpect(jsonPath("$.success.data.contents[2].contentId").value(secondId))
        .andExpect(jsonPath("$.success.data.contents[2].sortOrder").value(2));
  }

  @Test
  void reorderContentsReturnsBadRequestWhenIdsAreDuplicatedOrMissing() throws Exception {
    Display display = displayWithThreeContents();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    Long firstId = category.getContents().getFirst().getId();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/reorder",
                    savedDisplay.getId(),
                    category.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reorderRequest(List.of(firstId, firstId, firstId))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_CONTENT_ORDER"));
  }

  @Test
  void reorderContentsReturnsForbiddenWhenRequesterIsNotAcceptedTeamMember() throws Exception {
    Display display = displayWithThreeContents();
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    DisplayContentCategory category = savedDisplay.getContentCategories().getFirst();
    List<Long> orderedContentIds =
        category.getContents().stream().map(DisplayContent::getId).toList();

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/content-categories/{categoryId}/contents/reorder",
                    savedDisplay.getId(),
                    category.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(3L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reorderRequest(orderedContentIds)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_CONTENT_PERMISSION_DENIED"));
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private static String categoryRequest(String name, String description) {
    return """
        {
          "name": "%s",
          "description": "%s"
        }
        """
        .formatted(name, description);
  }

  private static String categoryRequestWithoutDescription(String name) {
    return """
        {
          "name": "%s"
        }
        """
        .formatted(name);
  }

  private static String contentRequest(String imageUrl) {
    return """
        {
          "imageUrl": "%s"
        }
        """
        .formatted(imageUrl);
  }

  private static String reorderRequest(List<Long> orderedContentIds) {
    return """
        {
          "orderedContentIds": %s
        }
        """
        .formatted(orderedContentIds);
  }

  private static Display displayWithMember() {
    Display display = display();
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }

  private static Display displayWithCategory() {
    Display display = displayWithMember();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시장 전경",
            "전시장 이미지입니다.",
            0,
            List.of(
                new DisplayContent(null, "https://cdn.displayu.com/display/content-1.jpg", 0))));
    return display;
  }

  private static Display displayWithThreeContents() {
    Display display = displayWithMember();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시장 전경",
            "전시장 이미지입니다.",
            0,
            List.of(
                new DisplayContent(null, "https://cdn.displayu.com/display/content-1.jpg", 0),
                new DisplayContent(null, "https://cdn.displayu.com/display/content-2.jpg", 1),
                new DisplayContent(null, "https://cdn.displayu.com/display/content-3.jpg", 2))));
    return display;
  }

  private static Display displayWithFullCategory() {
    Display display = displayWithMember();
    display.addContentCategory(
        new DisplayContentCategory(null, "전시장 전경", "전시장 이미지입니다.", 0, twentyContents()));
    return display;
  }

  private static List<DisplayContent> twentyContents() {
    return java.util.stream.IntStream.range(0, 20)
        .mapToObj(
            index ->
                new DisplayContent(
                    null,
                    "https://cdn.displayu.com/display/content-" + (index + 1) + ".jpg",
                    index))
        .toList();
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
        "",
        "",
        "organization",
        "department",
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
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
