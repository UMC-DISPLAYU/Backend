package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryRepository;
import com.example.demo.domain.display.application.query.DisplayInvitationDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({
  JpaDisplayInvitationDisplayQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaDisplayInvitationDisplayQueryRepositoryAdapterTest {

  @Autowired private DisplayInvitationDisplayQueryRepository queryRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void findPendingInvitationDisplaysProjectsOnlyCardFieldsWithMainImage() {
    Long inviteeUserId = 20L;
    persistDisplayWithInvitation(1, inviteeUserId, DisplayInvitationStatus.PENDING, null);
    persistDisplayWithInvitation(2, inviteeUserId, DisplayInvitationStatus.PENDING, null);
    persistDisplayWithInvitation(3, inviteeUserId, DisplayInvitationStatus.ACCEPTED, null);
    persistDisplayWithInvitation(
        4, inviteeUserId, DisplayInvitationStatus.PENDING, LocalDateTime.of(2026, 8, 1, 12, 0));
    entityManager.flush();
    entityManager.clear();

    Statistics statistics =
        entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
    statistics.setStatisticsEnabled(true);
    statistics.clear();

    List<DisplayInvitationDisplayQueryResult> results =
        queryRepository.findPendingInvitationDisplays(inviteeUserId);

    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(DisplayInvitationDisplayQueryResult::title)
        .containsExactly("FORM 2026 2", "FORM 2026 1");
    assertThat(results)
        .extracting(DisplayInvitationDisplayQueryResult::posterImageUrl)
        .containsExactly(
            "https://cdn.displayu.com/posters/main-2.png",
            "https://cdn.displayu.com/posters/main-1.png");
    assertThat(statistics.getEntityLoadCount()).isZero();
    assertThat(statistics.getPrepareStatementCount()).isEqualTo(1);
  }

  private void persistDisplayWithInvitation(
      int index, Long inviteeUserId, DisplayInvitationStatus status, LocalDateTime deletedAt) {
    Display display = display(index);
    display.addImage(
        new DisplayImage(
            null,
            "https://cdn.displayu.com/posters/detail-" + index + ".png",
            DisplayImageType.DETAIL,
            1,
            null));
    display.addInvitation(
        new DisplayInvitation(
            null,
            new UserId(10L),
            new UserId(inviteeUserId),
            status,
            LocalDateTime.of(2026, 8, 1, 10, index),
            status == DisplayInvitationStatus.PENDING
                ? null
                : LocalDateTime.of(2026, 8, 1, 11, index),
            deletedAt));
    entityManager.persist(display);
  }

  private static Display display(int index) {
    return Display.create(
        new UserId(10L),
        "FORM 2026 " + index,
        "https://cdn.displayu.com/posters/main-" + index + ".png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
        "@displayu",
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
