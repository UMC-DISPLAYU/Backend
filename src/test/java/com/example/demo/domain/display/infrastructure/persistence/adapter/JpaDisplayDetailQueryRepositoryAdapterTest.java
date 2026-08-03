package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayDetailQueryRepository;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayLikeJpaRepository;
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
  JpaDisplayDetailQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaDisplayDetailQueryRepositoryAdapterTest {

  @Autowired private DisplayDetailQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private SpringDataDisplayLikeJpaRepository displayLikeJpaRepository;

  @Test
  void findDisplayDetailProjectsDisplayDetailWithoutLoadingAggregate() {
    Display display = display();
    display.publish();
    display.addContentCategory(
        new DisplayContentCategory(
            null,
            "전시장 전경",
            "전시장 이미지입니다.",
            0,
            List.of(
                new DisplayContent(
                    null, "https://cdn.displayu.com/display/content-1.jpg", 1440, 960, 0),
                new DisplayContent(
                    null, "https://cdn.displayu.com/display/content-2.jpg", 1440, 960, 1))));
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    display.addInvitation(
        new DisplayInvitation(
            null,
            new UserId(1L),
            new UserId(3L),
            DisplayInvitationStatus.PENDING,
            LocalDateTime.of(2026, 7, 28, 10, 0),
            null,
            null));
    Display savedDisplay = displayJpaRepository.saveAndFlush(display);
    displayLikeJpaRepository.saveAndFlush(DisplayLike.create(savedDisplay.getId(), new UserId(5L)));

    DisplayDetailResult result =
        queryRepository.findDisplayDetail(savedDisplay.getId(), null).orElseThrow();

    assertThat(result.displayId()).isEqualTo(savedDisplay.getId());
    assertThat(result.ownerUserId()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("FORM 2026");
    assertThat(result.location().placeName()).isEqualTo("전시장");
    assertThat(result.displayType()).isEqualTo(DisplayType.GRADUATION.name());
    assertThat(result.displayFields()).containsExactly(DisplayField.DESIGN.name());
    assertThat(result.region()).isEqualTo(DisplayRegion.SEOUL.name());
    assertThat(result.status()).isEqualTo("PUBLISHED");
    assertThat(result.likeCount()).isEqualTo(1L);
    assertThat(result.images())
        .extracting(DisplayDetailResult.ImageResult::imageType)
        .containsExactly(DisplayImageType.MAIN.name());
    assertThat(result.contentCategories()).hasSize(1);
    assertThat(result.contentCategories().getFirst().contents())
        .extracting(DisplayDetailResult.ContentResult::sortOrder)
        .containsExactly(0, 1);
    assertThat(result.teamMembers())
        .extracting(DisplayDetailResult.TeamMemberResult::displayNickname)
        .containsExactly("팀원");
    assertThat(result.invitations())
        .extracting(DisplayDetailResult.InvitationResult::inviteeUserId)
        .containsExactly(3L);
  }

  @Test
  void findDisplayDetailReturnsEmptyWhenDisplayDoesNotExist() {
    assertThat(queryRepository.findDisplayDetail(999L, null)).isEmpty();
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
        "qna@displayu.com",
        "note",
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
