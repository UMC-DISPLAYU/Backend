package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
            new DisplayScreeningSearchQuery(null, DisplayStatus.PENDING_REVIEW, null, 20), 21);

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
            new DisplayScreeningSearchQuery(null, DisplayStatus.PUBLISHED, newer.getId(), 20), 21);

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
            new DisplayScreeningSearchQuery(null, DisplayStatus.PENDING_REVIEW, null, 20), 21);

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
  }

  @ParameterizedTest
  @ValueSource(strings = {"alpha", "중앙", "디자인", "%_"})
  void searchesTextFieldsAndKeepsStatusDeletedAndCursorFilters(String keyword) {
    Display matching = display("Alpha%_전시");
    Display other = display("다른 전시");
    other.changeBasicInfo("다른 전시", "부제", "내용", "", "", "다른학교", "다른학과");
    Display published = display("Alpha%_공개 전시");
    Display deleted = display("Alpha%_삭제 전시");
    displayJpaRepository.saveAllAndFlush(List.of(matching, other, published, deleted));
    matching.requestReview();
    other.requestReview();
    published.publish();
    deleted.requestReview();
    deleted.delete();
    displayJpaRepository.flush();
    assertThat(
            queryRepository.search(
                new DisplayScreeningSearchQuery(keyword, DisplayStatus.PENDING_REVIEW, null, 20),
                21))
        .extracting(DisplayScreeningSummaryQueryResult::displayId)
        .containsExactly(matching.getId());
    assertThat(
            queryRepository.search(
                new DisplayScreeningSearchQuery(
                    keyword, DisplayStatus.PENDING_REVIEW, matching.getId(), 20),
                21))
        .isEmpty();
  }

  @Test
  void searchesExactIdAndHandlesNumericOverflowAsText() {
    Display matching = display("대상 전시");
    Display other = display("다른 전시");
    displayJpaRepository.saveAllAndFlush(List.of(matching, other));
    matching.requestReview();
    other.requestReview();
    displayJpaRepository.flush();
    assertThat(
            queryRepository.search(
                new DisplayScreeningSearchQuery(
                    matching.getId().toString(), DisplayStatus.PENDING_REVIEW, null, 20),
                21))
        .extracting(DisplayScreeningSummaryQueryResult::displayId)
        .containsExactly(matching.getId());
    assertThat(
            queryRepository.search(
                new DisplayScreeningSearchQuery(
                    "9".repeat(100), DisplayStatus.PENDING_REVIEW, null, 20),
                21))
        .isEmpty();
    assertThat(
            queryRepository.search(
                new DisplayScreeningSearchQuery("  ", DisplayStatus.PENDING_REVIEW, null, 20), 21))
        .extracting(DisplayScreeningSummaryQueryResult::displayId)
        .containsExactly(other.getId(), matching.getId());
  }

  @Test
  void historyKeepsRejectedCancelledAndPendingRoundsNewestFirst() {
    Display display = displayJpaRepository.saveAndFlush(display("회차 전시"));
    LocalDateTime requestedAt = LocalDateTime.of(2026, 11, 11, 1, 0);
    display.requestReview();
    DisplayScreening rejected =
        screeningJpaRepository.saveAndFlush(DisplayScreening.request(display, 1L, requestedAt));
    rejected.reject(7L, "수정 요청", requestedAt.plusMinutes(1));
    display.rejectReview();
    display.requestReview();
    DisplayScreening cancelled =
        screeningJpaRepository.saveAndFlush(DisplayScreening.request(display, 2L, requestedAt));
    cancelled.cancel(2L, requestedAt.plusMinutes(2));
    display.cancelReview();
    display.requestReview();
    DisplayScreening pending =
        screeningJpaRepository.saveAndFlush(DisplayScreening.request(display, 3L, requestedAt));
    Display other = displayJpaRepository.saveAndFlush(display("다른 전시"));
    screeningJpaRepository.saveAndFlush(DisplayScreening.request(other, 9L, requestedAt));
    displayJpaRepository.flush();

    var result = detailService.getDetail(display.getId());
    assertThat(result.history())
        .extracting(item -> item.screeningId())
        .containsExactly(pending.getId(), cancelled.getId(), rejected.getId());
    assertThat(result.history())
        .extracting(item -> item.status())
        .containsExactly("PENDING_REVIEW", "CANCELLED", "REJECTED");
    assertThat(result.requesterId()).isEqualTo(3L);
    assertThat(result.screeningStatus()).isEqualTo("PENDING_REVIEW");
    assertThat(result.history().getFirst().requestedAt())
        .isEqualTo(requestedAt.toInstant(ZoneOffset.UTC));
    assertThat(result.history().getFirst().processedAt()).isNull();
    assertThat(result.history().get(1).cancelledBy()).isEqualTo(2L);
    assertThat(result.history().get(1).reviewerId()).isNull();
    assertThat(result.history().getLast().reviewerId()).isEqualTo(7L);
    assertThat(result.history().getLast().rejectionReason()).isEqualTo("수정 요청");
    assertThat(result.history().getLast().processedAt())
        .isEqualTo(requestedAt.plusMinutes(1).toInstant(ZoneOffset.UTC));
  }

  @Test
  void legacyDisplayHasEmptyHistoryAndDeletedDisplayIsNotExposed() {
    Display legacy = display("기존 전시");
    legacy.publish();
    displayJpaRepository.saveAndFlush(legacy);
    var detail = detailService.getDetail(legacy.getId());
    assertThat(detail.history()).isEmpty();
    assertThat(detail.requesterId()).isNull();
    assertThat(detail.screeningStatus()).isNull();
    legacy.delete();
    displayJpaRepository.flush();
    assertThatThrownBy(() -> detailService.getDetail(legacy.getId()))
        .isInstanceOfSatisfying(
            BusinessException.class,
            e -> assertThat(e.errorCode()).isEqualTo(GlobalErrorCode.NOT_FOUND));
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
