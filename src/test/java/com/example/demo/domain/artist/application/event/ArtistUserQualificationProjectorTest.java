package com.example.demo.domain.artist.application.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArtistUserQualificationProjectorTest {

  private static final Long USER_ID = 1L;
  private static final Instant OCCURRED_AT = Instant.parse("2026-09-27T00:00:00Z");
  private final FakeRepository repository = new FakeRepository();
  private final ArtistUserQualificationProjector projector =
      new ArtistUserQualificationProjector(repository);

  @Test
  void createsAndUpdatesProjectionForEveryUserEvent() {
    projector.project(
        new UserSchoolVerifiedEvent(
            UUID.randomUUID(), USER_ID, "student@du.ac.kr", "덕성여자대학교", 1L, OCCURRED_AT));
    projector.project(
        new UserUniversityChangedEvent(
            UUID.randomUUID(), USER_ID, "변경대학교", 2L, OCCURRED_AT.plusSeconds(1)));
    projector.project(
        new UserWithdrawnEvent(UUID.randomUUID(), USER_ID, 3L, OCCURRED_AT.plusSeconds(2)));

    ArtistUserQualification qualification = repository.findByUserId(USER_ID).orElseThrow();
    assertThat(qualification.getSchoolEmail()).isEqualTo("student@du.ac.kr");
    assertThat(qualification.getUniversityName()).isEqualTo("변경대학교");
    assertThat(qualification.isSchoolVerified()).isTrue();
    assertThat(qualification.isWithdrawn()).isTrue();
    assertThat(repository.saveCount).isEqualTo(3);
  }

  @Test
  void doesNotSaveDuplicateOrOlderEvent() {
    UUID eventId = UUID.randomUUID();
    projector.project(
        new UserSchoolVerifiedEvent(
            eventId, USER_ID, "student@du.ac.kr", "덕성여자대학교", 2L, OCCURRED_AT));

    projector.project(
        new UserSchoolVerifiedEvent(
            eventId, USER_ID, "student@du.ac.kr", "중복대학교", 2L, OCCURRED_AT));
    projector.project(
        new UserUniversityChangedEvent(
            UUID.randomUUID(), USER_ID, "과거대학교", 1L, OCCURRED_AT.minusSeconds(1)));

    assertThat(repository.saveCount).isEqualTo(1);
    assertThat(repository.findByUserId(USER_ID).orElseThrow().getUniversityName())
        .isEqualTo("덕성여자대학교");
  }

  private static class FakeRepository implements ArtistUserQualificationRepository {

    private final Map<Long, ArtistUserQualification> storage = new HashMap<>();
    private int saveCount;

    @Override
    public Optional<ArtistUserQualification> findByUserId(Long userId) {
      return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public ArtistUserQualification save(ArtistUserQualification qualification) {
      storage.put(qualification.getUserId(), qualification);
      saveCount++;
      return qualification;
    }
  }
}
