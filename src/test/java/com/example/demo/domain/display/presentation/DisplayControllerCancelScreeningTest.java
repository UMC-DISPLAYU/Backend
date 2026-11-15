package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.application.command.ApproveDisplayScreeningService;
import com.example.demo.domain.display.application.command.CancelDisplayScreeningService;
import com.example.demo.domain.display.application.query.GetDisplayScreeningDetailService;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.repository.DisplayRepository;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayScreeningJpaRepository;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerCancelScreeningTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private SpringDataDisplayScreeningJpaRepository screeningJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Autowired
  private com.example.demo.domain.display.application.query.SearchDisplayScreeningsService
      screeningSearchService;

  @Autowired private CancelDisplayScreeningService cancelService;

  @Autowired private ApproveDisplayScreeningService approveService;

  @Autowired private DisplayRepository displayRepository;

  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private EntityManager entityManager;

  @Autowired private GetDisplayScreeningDetailService screeningDetailService;

  private DisplayScreening submit(Display display) {
    display.requestReview();
    displayJpaRepository.saveAndFlush(display);
    return screeningJpaRepository.saveAndFlush(
        DisplayScreening.request(display, 1L, LocalDateTime.now()));
  }

  @Test
  void cancelsPreservesDataAndRejectsStaleRequestsAfterResubmission() throws Exception {
    Display display = displayWithTeamMembers();
    var screening = submit(display);
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/display/{id}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.currentScreeningId").value(screening.getId()));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/api/v1/display/{id}/screenings/{sid}/cancel",
                    display.getId(),
                    screening.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.status").value("DRAFT"))
        .andExpect(jsonPath("$.success.data.screeningStatus").value("CANCELLED"));
    entityManager.flush();
    entityManager.clear();
    var saved = displayJpaRepository.findById(display.getId()).orElseThrow();
    Assertions.assertThat(saved.getTitle()).isEqualTo("FORM 2026");
    Assertions.assertThat(saved.getImages()).hasSize(1);
    Assertions.assertThat(saved.getTeamMembers()).hasSize(2);
    Assertions.assertThat(
            screeningSearchService
                .search(
                    new com.example.demo.domain.display.application.query
                        .DisplayScreeningSearchQuery(
                        null,
                        com.example.demo.domain.display.domain.type.DisplayStatus.PENDING_REVIEW,
                        null,
                        100))
                .items())
        .extracting(
            com.example.demo.domain.display.application.result.DisplayScreeningSummaryResult
                ::displayId)
        .doesNotContain(display.getId());
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/display/{id}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.currentScreeningId").doesNotExist());
    var history = screeningJpaRepository.findById(screening.getId()).orElseThrow();
    Assertions.assertThat(history.getCancelledBy()).isEqualTo(1L);
    Assertions.assertThat(history.getProcessedAt()).isNotNull();
    Assertions.assertThat(screeningDetailService.getDetail(display.getId()).screeningStatus())
        .isEqualTo("CANCELLED");
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/api/v1/display/{id}/screenings/{sid}/cancel",
                    display.getId(),
                    screening.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isConflict());
    mockMvc
        .perform(
            patch("/api/v1/display/publish")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"displayId\":" + display.getId() + "}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.currentScreeningId").isNumber());
    var next =
        screeningJpaRepository.findFirstByDisplayIdOrderByIdDesc(display.getId()).orElseThrow();
    Assertions.assertThat(next.getId()).isNotEqualTo(screening.getId());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/api/v1/display/{id}/screenings/{sid}/cancel",
                    display.getId(),
                    screening.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isConflict());
    Assertions.assertThat(next.getStatus().name()).isEqualTo("PENDING_REVIEW");
  }

  @Test
  void enforcesAuthenticationOwnershipAndTarget() throws Exception {
    Display display = displayWithTeamMembers();
    var screening = submit(display);
    String path =
        "/api/v1/display/" + display.getId() + "/screenings/" + screening.getId() + "/cancel";
    mockMvc.perform(MockMvcRequestBuilders.post(path)).andExpect(status().isUnauthorized());
    for (long userId : new long[] {2L, 3L}) {
      mockMvc
          .perform(
              MockMvcRequestBuilders.post(path).header(HttpHeaders.AUTHORIZATION, bearer(userId)))
          .andExpect(status().isForbidden());
    }
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/api/v1/display/{id}/screenings/{sid}/cancel", display.getId(), 999999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound());
    Display other = displayJpaRepository.saveAndFlush(displayWithTeamMembers());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                    "/api/v1/display/{id}/screenings/{sid}/cancel",
                    other.getId(),
                    screening.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound());
  }

  @Test
  void rejectsApprovedAndRejectedRequests() throws Exception {
    for (boolean approve : new boolean[] {true, false}) {
      Display display = displayWithTeamMembers();
      var screening = submit(display);
      if (approve) {
        display.approveReview();
        screening.approve(7L, LocalDateTime.now());
      } else {
        display.rejectReview();
        screening.reject(7L, "수정", LocalDateTime.now());
      }
      mockMvc
          .perform(
              MockMvcRequestBuilders.post(
                      "/api/v1/display/{id}/screenings/{sid}/cancel",
                      display.getId(),
                      screening.getId())
                  .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
          .andExpect(status().isConflict());
    }
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void concurrentApprovalAndCancellationCommitOnlyOneOutcome() throws Exception {
    var tx = new TransactionTemplate(transactionManager);
    long[] ids =
        tx.execute(
            status -> {
              var screening = submit(displayWithTeamMembers());
              return new long[] {screening.getDisplay().getId(), screening.getId()};
            });
    var barrier = new CyclicBarrier(2);
    var executor = Executors.newFixedThreadPool(2);
    try {
      var outcomes = new ArrayList<Future<Boolean>>();
      for (boolean approve : new boolean[] {true, false}) {
        outcomes.add(
            executor.submit(
                () -> {
                  try {
                    tx.executeWithoutResult(
                        status -> {
                          displayRepository.findByIdWithOptimisticLock(ids[0]).orElseThrow();
                          screeningJpaRepository.findById(ids[1]).orElseThrow();
                          try {
                            barrier.await(10, TimeUnit.SECONDS);
                          } catch (Exception e) {
                            throw new IllegalStateException(e);
                          }
                          if (approve) approveService.approve(ids[0], 7L);
                          else cancelService.cancel(ids[0], ids[1], 1L);
                        });
                    return true;
                  } catch (OptimisticLockingFailureException e) {
                    return false;
                  }
                }));
      }
      Assertions.assertThat(outcomes.get(0).get(20, TimeUnit.SECONDS))
          .isNotEqualTo(outcomes.get(1).get(20, TimeUnit.SECONDS));
      tx.executeWithoutResult(
          status -> {
            var display = displayJpaRepository.findById(ids[0]).orElseThrow();
            var screening = screeningJpaRepository.findById(ids[1]).orElseThrow();
            if (display.isPublished()) {
              Assertions.assertThat(screening.getStatus().name()).isEqualTo("PUBLISHED");
              Assertions.assertThat(screening.getCancelledBy()).isNull();
            } else {
              Assertions.assertThat(display.getStatus().name()).isEqualTo("DRAFT");
              Assertions.assertThat(screening.getStatus().name()).isEqualTo("CANCELLED");
              Assertions.assertThat(screening.getReviewerId()).isNull();
            }
          });
    } finally {
      executor.shutdownNow();
      tx.executeWithoutResult(
          status -> {
            screeningJpaRepository.deleteById(ids[1]);
            screeningJpaRepository.flush();
            displayJpaRepository.deleteById(ids[0]);
          });
    }
  }

  private static Display displayWithTeamMembers() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "전시 부제",
            "전시 설명",
            new DisplayLocation(
                "중앙대학교 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "전시 유의사항",
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
