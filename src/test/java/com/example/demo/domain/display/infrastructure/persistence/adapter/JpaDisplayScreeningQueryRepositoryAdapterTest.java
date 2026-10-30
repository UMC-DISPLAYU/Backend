package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayScreeningQueryRepository;
import com.example.demo.domain.display.application.query.DisplayScreeningSearchQuery;
import com.example.demo.domain.display.application.query.DisplayScreeningSummaryQueryResult;
import com.example.demo.domain.display.application.query.GetDisplayScreeningDetailService;
import com.example.demo.domain.display.application.result.DisplayScreeningDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayScreeningJpaRepository;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({
  JpaDisplayScreeningQueryRepositoryAdapter.class,
  JpaDisplayRepositoryAdapter.class,
  JpaDisplayScreeningRepositoryAdapter.class,
  JpaTeamMemberRepositoryAdapter.class,
  GetDisplayScreeningDetailService.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaDisplayScreeningQueryRepositoryAdapterTest {

  @Autowired private DisplayScreeningQueryRepository queryRepository;
  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;
  @Autowired private SpringDataDisplayScreeningJpaRepository screeningJpaRepository;
  @Autowired private GetDisplayScreeningDetailService detailService;

  @Test
  void searchReturnsPendingReviewContractFields() {
    Display display = display("심사 전시");
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "대표자", TeamMemberRole.TEAM_LEADER, true));
    displayJpaRepository.saveAndFlush(display);
    display.requestReview();
    screeningJpaRepository.saveAndFlush(
        DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 20, 1, 0)));

    List<DisplayScreeningSummaryQueryResult> results =
        queryRepository.search(
            new DisplayScreeningSearchQuery(DisplayStatus.PENDING_REVIEW, null, null, 20), 21);

    assertThat(results).hasSize(1);
    DisplayScreeningSummaryQueryResult result = results.getFirst();
    assertThat(result.displayId()).isEqualTo(display.getId());
    assertThat(result.requesterId()).isEqualTo(1L);
    assertThat(result.school()).isEqualTo("중앙대학교");
    assertThat(result.leaderName()).isEqualTo("대표자");
    assertThat(result.startDate()).isEqualTo(LocalDate.of(2026, 10, 1));
    assertThat(result.endDate()).isEqualTo(LocalDate.of(2026, 10, 7));
    assertThat(result.posterImageUrl()).isEqualTo("https://cdn.displayu.com/poster.png");
  }

  @Test
  void searchKeepsLegacyPublishedMetadataNullAndAppliesCursor() {
    Display older = display("기존 공개 전시");
    older.publish();
    Display newer = display("커서 기준 전시");
    newer.publish();
    displayJpaRepository.saveAllAndFlush(List.of(older, newer));

    List<DisplayScreeningSummaryQueryResult> results =
        queryRepository.search(
            new DisplayScreeningSearchQuery(DisplayStatus.PUBLISHED, null, newer.getId(), 20), 21);

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().displayId()).isEqualTo(older.getId());
    assertThat(results.getFirst().requesterId()).isNull();
    assertThat(results.getFirst().requestedAt()).isNull();
  }

  @Test
  void searchReturnsNullWhenLeaderCannotBeIdentifiedUniquely() {
    Display display = display("복수 대표 전시");
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "대표자1", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "대표자2", TeamMemberRole.TEAM_LEADER, true));
    displayJpaRepository.saveAndFlush(display);
    display.requestReview();
    screeningJpaRepository.saveAndFlush(
        DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 20, 1, 0)));

    List<DisplayScreeningSummaryQueryResult> results =
        queryRepository.search(
            new DisplayScreeningSearchQuery(DisplayStatus.PENDING_REVIEW, null, null, 20), 21);

    assertThat(results)
        .singleElement()
        .extracting(DisplayScreeningSummaryQueryResult::leaderName)
        .isNull();
  }

  @Test
  void detailReturnsLatestScreeningAndDisplayPreview() {
    Display display = display("상세 전시");
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "대표자", TeamMemberRole.TEAM_LEADER, true));
    displayJpaRepository.saveAndFlush(display);
    display.requestReview();
    DisplayScreening screening =
        DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 20, 1, 0));
    screeningJpaRepository.saveAndFlush(screening);
    display.rejectReview();
    screening.reject(7L, "일정 수정", LocalDateTime.of(2026, 9, 20, 2, 0));
    displayJpaRepository.flush();

    DisplayScreeningDetailResult result = detailService.getDetail(display.getId());

    assertThat(result.status()).isEqualTo("REJECTED");
    assertThat(result.requesterId()).isEqualTo(1L);
    assertThat(result.reviewerId()).isEqualTo(7L);
    assertThat(result.rejectionReason()).isEqualTo("일정 수정");
    assertThat(result.location()).isEqualTo("전시장");
    assertThat(result.imageUrls()).containsExactly("https://cdn.displayu.com/poster.png");
    assertThat(result.school()).isEqualTo("중앙대학교");
    assertThat(result.leaderName()).isEqualTo("대표자");
    assertThat(result.history())
        .singleElement()
        .satisfies(
            history -> {
              assertThat(history.status()).isEqualTo("REJECTED");
              assertThat(history.rejectionReason()).isEqualTo("일정 수정");
            });
  }

  @Test
  void detailListsEveryRequestNewestFirst() {
    Display display = display("재요청 전시");
    displayJpaRepository.saveAndFlush(display);
    display.requestReview();
    DisplayScreening first =
        DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 20, 1, 0));
    screeningJpaRepository.saveAndFlush(first);
    display.rejectReview();
    first.reject(7L, "포스터 교체", LocalDateTime.of(2026, 9, 20, 2, 0));
    display.requestReview();
    screeningJpaRepository.saveAndFlush(
        DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 21, 1, 0)));
    displayJpaRepository.flush();

    DisplayScreeningDetailResult result = detailService.getDetail(display.getId());

    assertThat(result.status()).isEqualTo("PENDING_REVIEW");
    assertThat(result.rejectionReason()).isNull();
    assertThat(result.history())
        .extracting(history -> history.status() + ":" + history.rejectionReason())
        .containsExactly("PENDING_REVIEW:null", "REJECTED:포스터 교체");
  }

  @Test
  void detailReturnsEmptyHistoryWithoutRequests() {
    Display display = display("기존 공개 전시");
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    assertThat(detailService.getDetail(display.getId()).history()).isEmpty();
  }

  @Test
  void searchMatchesTitleSchoolDepartmentOrId() {
    Display byTitle = display("빛의 정원");
    Display byDepartment = display("다른 전시");
    byDepartment.changeBasicInfo(
        byDepartment.getTitle(),
        byDepartment.getSubtitle(),
        byDepartment.getContent(),
        byDepartment.getQnaAccount(),
        byDepartment.getNote(),
        "홍익대학교",
        "조소과");
    displayJpaRepository.saveAllAndFlush(List.of(byTitle, byDepartment));
    for (Display display : List.of(byTitle, byDepartment)) {
      display.requestReview();
      screeningJpaRepository.saveAndFlush(
          DisplayScreening.request(display, 1L, LocalDateTime.of(2026, 9, 20, 1, 0)));
    }
    displayJpaRepository.flush();

    assertThat(searchIds("정원")).containsExactly(byTitle.getId());
    assertThat(searchIds("홍익")).containsExactly(byDepartment.getId());
    assertThat(searchIds("조소")).containsExactly(byDepartment.getId());
    assertThat(searchIds(String.valueOf(byTitle.getId()))).contains(byTitle.getId());
    assertThat(searchIds("없는 전시")).isEmpty();
  }

  private List<Long> searchIds(String keyword) {
    return queryRepository
        .search(
            new DisplayScreeningSearchQuery(DisplayStatus.PENDING_REVIEW, keyword, null, 20), 21)
        .stream()
        .map(DisplayScreeningSummaryQueryResult::displayId)
        .toList();
  }

  private static Display display(String title) {
    return Display.create(
        new UserId(1L),
        title,
        "https://cdn.displayu.com/poster.png",
        "부제",
        "내용",
        new DisplayLocation("전시장", new BigDecimal("37.5"), new BigDecimal("126.9")),
        "",
        "",
        "중앙대학교",
        "디자인학부",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        new DisplayPeriod(
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 10, 7),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }
}
