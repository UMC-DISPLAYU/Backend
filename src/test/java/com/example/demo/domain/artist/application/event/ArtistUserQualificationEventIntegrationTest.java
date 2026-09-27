package com.example.demo.domain.artist.application.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.artist.infrastructure.persistence.ArtistUserQualificationJpaEntity;
import com.example.demo.domain.artist.infrastructure.persistence.ArtistUserQualificationJpaRepository;
import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@ActiveProfiles("test")
class ArtistUserQualificationEventIntegrationTest {

  @Autowired private ApplicationEventPublisher eventPublisher;
  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private ArtistUserQualificationJpaRepository qualificationRepository;
  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    qualificationRepository.deleteAll();
    jdbcTemplate.update("DELETE FROM EVENT_PUBLICATION");
  }

  @Test
  void recordsPublicationAndCompletesArtistProjection() throws Exception {
    UUID eventId = UUID.randomUUID();
    UserSchoolVerifiedEvent event =
        new UserSchoolVerifiedEvent(
            eventId,
            101L,
            "student@du.ac.kr",
            "덕성여자대학교",
            1L,
            Instant.parse("2026-09-27T00:00:00Z"));

    new TransactionTemplate(transactionManager)
        .executeWithoutResult(status -> eventPublisher.publishEvent(event));

    ArtistUserQualificationJpaEntity projection = awaitProjection(101L);
    assertThat(projection.isSchoolVerified()).isTrue();
    assertThat(projection.getLastEventId()).isEqualTo(eventId);
    awaitCompletedPublication();
  }

  private ArtistUserQualificationJpaEntity awaitProjection(Long userId) throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (System.nanoTime() < deadline) {
      var projection = qualificationRepository.findById(userId);
      if (projection.isPresent()) {
        return projection.get();
      }
      Thread.sleep(20);
    }
    return qualificationRepository.findById(userId).orElseThrow();
  }

  private void awaitCompletedPublication() throws Exception {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
    while (System.nanoTime() < deadline) {
      Long count =
          jdbcTemplate.queryForObject(
              "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE STATUS = 'COMPLETED'", Long.class);
      if (count != null && count == 1L) {
        return;
      }
      Thread.sleep(20);
    }
    assertThat(
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM EVENT_PUBLICATION WHERE STATUS = 'COMPLETED'", Long.class))
        .isEqualTo(1L);
  }
}
