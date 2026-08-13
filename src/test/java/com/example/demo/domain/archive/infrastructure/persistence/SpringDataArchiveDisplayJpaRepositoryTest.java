package com.example.demo.domain.archive.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpringDataArchiveDisplayJpaRepositoryTest {

  @Autowired private SpringDataArchiveDisplayJpaRepository repository;

  @Autowired private SpringDataDisplayJpaRepository displayRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void findByUserIdBeforeCursorContinuesWhenCursorRecordIsDeleted() {
    Display cursorDisplay = display("커서 전시");
    Display nextDisplay = display("다음 전시");
    displayRepository.saveAllAndFlush(List.of(cursorDisplay, nextDisplay));

    ArchiveDisplay cursor =
        repository.saveAndFlush(ArchiveDisplay.create(cursorDisplay.getId(), 7L));
    ArchiveDisplay next = repository.saveAndFlush(ArchiveDisplay.create(nextDisplay.getId(), 7L));

    updateSavedAt(cursor.getId(), LocalDateTime.of(2026, 8, 13, 12, 0));
    updateSavedAt(next.getId(), LocalDateTime.of(2026, 8, 13, 11, 0));
    cursor.delete();
    repository.saveAndFlush(cursor);
    entityManager.clear();

    List<ArchiveDisplay> results =
        repository.findByUserIdBeforeCursorOrderBySavedAtDescIdDesc(
            7L, cursor.getId(), PageRequest.of(0, 10));

    assertThat(results).extracting(ArchiveDisplay::getId).containsExactly(next.getId());
  }

  private void updateSavedAt(Long id, LocalDateTime savedAt) {
    entityManager
        .createQuery("UPDATE ArchiveDisplay ad SET ad.savedAt = :savedAt WHERE ad.id = :id")
        .setParameter("savedAt", savedAt)
        .setParameter("id", id)
        .executeUpdate();
  }

  private static Display display(String title) {
    Display display =
        Display.create(
            new UserId(1L),
            title,
            "https://cdn.displayu.com/posters/main.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            new DisplayPeriod(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 10),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    return display;
  }
}
