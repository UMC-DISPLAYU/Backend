package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.infrastructure.adapter.DisplayReviewAdapter;
import com.example.demo.domain.admin.presentation.mapper.AdminReviewMapper;
import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.presentation.mapper.ArchivePresentationMapper;
import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryRepository;
import com.example.demo.domain.display.application.query.DisplaySummaryQueryRepository;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.MyDisplayInvitationListResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.PosterCrop;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.presentation.mapper.DisplayMemberInvitationPresentationMapper;
import com.example.demo.domain.display.presentation.response.PosterCropResponse;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayPosterCropTest {
  @Autowired private MockMvc mvc;
  @Autowired private SpringDataDisplayJpaRepository repository;
  @Autowired private EntityManager entityManager;
  @Autowired private Clock clock;
  @Autowired private CacheManager cacheManager;
  @Autowired private JwtFactory jwtFactory;
  @Autowired private DisplaySummaryQueryRepository summaries;
  @Autowired private ArchivePresentationMapper archiveMapper;
  @Autowired private DisplayInvitationDisplayQueryRepository invitationDisplays;
  @Autowired private DisplayMemberInvitationPresentationMapper invitationMapper;
  @Autowired private DisplayReviewAdapter reviews;
  @Autowired private AdminReviewMapper reviewMapper;

  @BeforeEach
  @AfterEach
  void clearCaches() {
    cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
  }

  @Test
  void persistedCropReachesEveryCardPathAndCachedResponses() throws Exception {
    Display display = display();
    display.changePosterCrop(
        new PosterCrop(
            new BigDecimal("0.1"),
            new BigDecimal("0.2"),
            new BigDecimal("0.8"),
            new BigDecimal("0.7")));
    display.addInvitation(
        new DisplayInvitation(
            null, new UserId(1L), new UserId(2L), LocalDateTime.now(clock), null));
    repository.saveAndFlush(display);
    Long id = display.getId();
    entityManager.clear();

    for (String path :
        List.of(
            "/api/v1/display/popular",
            "/api/v1/display/closing-soon",
            "/api/v1/display/graduation",
            "/api/v1/display/search")) {
      for (int request = 0; request < 2; request++) {
        var requestBuilder = get(path).param("size", "10");
        if (path.endsWith("/search")) {
          requestBuilder.param("cursor", "0");
        }
        mvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success.data.exhibitions[0].posterCrop.x").value(0.1))
            .andExpect(jsonPath("$.success.data.exhibitions[0].posterCrop.h").value(0.7));
      }
    }
    mvc.perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37")
                .param("northLatitude", "38")
                .param("westLongitude", "126")
                .param("eastLongitude", "128"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.markers[0].posterCrop.w").value(0.8));
    mvc.perform(
            get("/api/v1/display/me")
                .header("Authorization", "Bearer " + jwtFactory.create("1", 3600000L, "ACCESS")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.createdDisplays[0].posterCrop.y").value(0.2));
    mvc.perform(get("/api/v1/display/{displayId}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.posterCrop.w").value(0.8));

    PosterCropResponse expected =
        new PosterCropResponse(
            new BigDecimal("0.1000000000"),
            new BigDecimal("0.2000000000"),
            new BigDecimal("0.8000000000"),
            new BigDecimal("0.7000000000"));
    var summary = DisplaySummaryResult.from(summaries.findByDisplayIdIn(List.of(id)).getFirst());
    assertThat(
            archiveMapper
                .toResponse(
                    ArchiveDisplayResult.from(
                        ArchiveDisplay.create(id, 1L), null, summary, LocalDate.now(clock)))
                .posterCrop())
        .isEqualTo(expected);
    var invited =
        GraduationDisplayResult.ExhibitionResult.from(
            invitationDisplays.findPendingInvitationDisplays(2L).getFirst(), LocalDate.now(clock));
    assertThat(invited.withArchived(true).posterCrop()).isEqualTo(summary.posterCrop());
    assertThat(
            invitationMapper
                .toInvitationDisplayResponse(new GraduationDisplayResult(List.of(invited)))
                .exhibitions()
                .getFirst()
                .posterCrop())
        .isEqualTo(expected);
    var loaded = repository.findById(id).orElseThrow();
    assertThat(
            invitationMapper
                .toResponse(
                    MyDisplayInvitationListResult.from(loaded.getInvitations(), Map.of(), Map.of()))
                .invitations()
                .getFirst()
                .posterCrop())
        .isEqualTo(expected);
    assertThat(
            reviewMapper
                .toResponse(reviews.search(new ReviewSearchQuery("PUBLISHED", null, 10)))
                .items()
                .getFirst()
                .posterCrop())
        .isEqualTo(expected);
  }

  @Test
  void legacyRowsReturnNoCrop() {
    Display display = repository.saveAndFlush(display());
    entityManager.clear();
    assertThat(repository.findById(display.getId()).orElseThrow().getPosterCrop()).isNull();
    assertThat(summaries.findByDisplayIdIn(List.of(display.getId())).getFirst().posterCrop())
        .isNull();
  }

  private Display display() {
    LocalDate today = LocalDate.now(clock);
    Display display =
        Display.create(
            new UserId(1L),
            "크롭 전시",
            "https://example.com/poster.png",
            "부제",
            "설명",
            new DisplayLocation("전시장", new BigDecimal("37.5"), new BigDecimal("127")),
            "",
            "",
            "학교",
            "학과",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                today.minusDays(1), today.plusDays(1), LocalTime.of(10, 0), LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    display.publish();
    return display;
  }
}
